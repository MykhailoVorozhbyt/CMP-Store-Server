package com.feature.authentication.presentation

import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.domain.model.SignInResult
import com.feature.authentication.domain.model.request.AuthUserRequest
import com.feature.authentication.domain.repository.GoogleSignInService
import com.feature.authentication.domain.usecases.SignInUseCase
import com.feature.authentication.presentation.handler.SignInFailureHandler
import com.feature.authentication.presentation.social_media.SocialMediaViewAction
import com.feature.authentication.presentation.validator.AuthenticationValidator
import com.feature.authentication.presentation.view_data.AuthenticationInitializer
import com.feature.authentication.presentation.view_data.AuthenticationUiEvent
import com.feature.authentication.presentation.view_data.AuthenticationViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.domain.onError
import com.store.core.domain.onSuccess
import com.store.core.presentation.core.di.coroutines.AppDispatchers
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.scopeFor
import com.store.core.presentation.utils.UiText
import com.store.core.resources.Res
import com.store.core.resources.auth_success
import com.store.core.resources.auth_success_already_registered
import com.store.core.resources.common_error_unknown
import com.store.core.utils.Logger
import com.store.core.utils.i
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.cmp.store.getPlatform
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class AuthenticationViewModel(
    private val signInUseCase: SignInUseCase,
    private val googleSignInService: GoogleSignInService,
    private val validator: AuthenticationValidator,
    private val signInFailureHandler: SignInFailureHandler,
    dispatchers: AppDispatchers
) : BaseActionHandleViewModel<AuthenticationViewData>(dispatchers) {
    override val _viewData = MutableStateFlow(AuthenticationViewData())

    private val initializer = AuthenticationInitializer()
    private val actionHandleScope = scopeFor(_viewData)

    init {
        initializer.initialize(_viewData)
    }

    override suspend fun handleViewAction(action: ViewAction) {
        when (action) {
            is InputFieldChanged -> validator.validateAdvanced(action, actionHandleScope)
            is AuthenticationViewAction.OnSignInClick -> handleManualSignIn()
            is SocialMediaViewAction.OnGoogleClick -> handleGoogleClick()
            is SocialMediaViewAction.OnSignInFailure -> handleGoogleSignInFailure(action.exception)
            is SocialMediaViewAction.OnSignInSuccess -> handleAuthenticatedUser(action.user)
        }
    }

    private fun handleManualSignIn() {
        launchIo {
            setManualLoading(true)
            val manual = _viewData.value.manual
            handleAuthorizeResult(
                result = signInUseCase(
                    email = manual.email.input,
                    password = manual.password.input
                )
            )
        }
    }

    private fun handleGoogleClick() {
        launchIo {
            setGoogleLoading(true)
            if (getPlatform().isDesktop) {
                googleSignIn()
            }
        }
    }

    suspend fun googleSignIn() {
        googleSignInService.signIn()
            .onSuccess { handleAuthenticatedUser(it) }
            .onError { handleGoogleSignInFailure(it) }
    }

    private suspend fun handleGoogleSignInFailure(error: GoogleSignInError) {
        Logger.i("OnGoogleSignInFailure: $error")
        clearLoading()
        showError(signInFailureHandler.handle(error))
    }

    private suspend fun handleAuthenticatedUser(user: FirebaseUser?) {
        if (user == null) {
            clearLoading()
            showError(getString(Res.string.common_error_unknown))
        } else {
            handleAuthenticatedUser(
                AuthUserRequest(
                    uid = user.googleUid(),
                    displayName = user.displayName,
                    email = user.email,
                )
            )
        }
    }

    private fun FirebaseUser.googleUid(): String? =
        providerData.firstOrNull { it.providerId == GOOGLE_PROVIDER_ID }?.uid

    private suspend fun handleAuthenticatedUser(user: AuthUserRequest?) {
        handleAuthorizeResult(signInUseCase(user))
    }

    private suspend fun handleAuthorizeResult(result: SignInResult) {
        clearLoading()
        when (result) {
            is SignInResult.Success -> emitEvent(
                AuthenticationUiEvent.ToMain(
                    UiText.Resource(getAuthSuccessResource(result))
                )
            )

            is SignInResult.Failure -> showError(signInFailureHandler.handle(result.error))
        }
    }

    private fun getAuthSuccessResource(result: SignInResult.Success): StringResource =
        if (result.isNewReg) Res.string.auth_success
        else Res.string.auth_success_already_registered

    private fun clearLoading() {
        setManualLoading(false)
        setGoogleLoading(false)
    }

    private fun setGoogleLoading(isLoading: Boolean) {
        _viewData.update {
            it.copy(
                socialMedia = it.socialMedia.copy(
                    google = it.socialMedia.google.copy(isLoading = isLoading)
                )
            )
        }
    }

    private fun setManualLoading(isLoading: Boolean) {
        _viewData.update {
            it.copy(
                isLoading = isLoading,
                manual = it.manual.copy(
                    email = it.manual.email.copy(enabled = !isLoading),
                    password = it.manual.password.copy(enabled = !isLoading),
                )
            )
        }
    }

    companion object {
        private const val GOOGLE_PROVIDER_ID = "google.com"
    }
}
