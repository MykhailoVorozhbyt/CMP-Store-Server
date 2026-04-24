package com.feature.authentication.presentation

import androidx.lifecycle.viewModelScope
import com.feature.authentication.domain.model.onFailure
import com.feature.authentication.domain.model.onSuccess
import com.feature.authentication.domain.model.onUserAlreadyExists
import com.feature.authentication.domain.usecases.CreateCustomerUseCase
import com.feature.authentication.presentation.social_media.SocialMediaViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import com.store.core.resources.Res
import com.store.core.resources.authentication_successful
import com.store.core.resources.authentication_successful_account_exist
import com.store.core.resources.internet_connection_unavailable
import com.store.core.resources.sign_in_canceled
import com.store.core.utils.Logger
import com.store.core.utils.i
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AuthenticationViewModel(
    private val useCase: CreateCustomerUseCase,
    @MainDispatcher mainDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : BaseActionHandleViewModel<AuthenticationViewData>(
    mainDispatcher, ioDispatcher
) {
    override val _viewData = MutableStateFlow(AuthenticationViewData())

    override suspend fun handleViewAction(action: ViewAction) {
        when (action) {
            SocialMediaViewAction.OnGoogleClick -> handleGoogleClick()
            is SocialMediaViewAction.OnGoogleSignInFailure -> handleGoogleSignInFailure(action)
            is SocialMediaViewAction.OnGoogleSignInSuccess -> handleGoogleSignInSuccess(action)
        }
    }

    private fun handleGoogleClick() {
        viewModelScope.launch {
            setGoogleLoading(true)
        }
    }

    private fun handleGoogleSignInSuccess(action: SocialMediaViewAction.OnGoogleSignInSuccess) {
        Logger.i("OnGoogleSignInSuccess: ${action.user}")
        viewModelScope.launch {
            useCase.invoke(action.user)
                .onSuccess {
                    Logger.i("createCustomer: $it")
                    emitEvent(AuthenticationViewAction.ToMainScreen(getString(Res.string.authentication_successful)))
                }
                .onUserAlreadyExists {
                    emitEvent(AuthenticationViewAction.ToMainScreen(getString(Res.string.authentication_successful_account_exist)))
                }
                .onFailure {
                    Logger.i("createCustomer: ${it.errorCode}")
                    showError(it.errorCode)
                }.also {
                    setGoogleLoading(false)
                }
        }
    }

    private suspend fun handleGoogleSignInFailure(action: SocialMediaViewAction.OnGoogleSignInFailure) {
        val message = action.exception.message
        Logger.i("OnGoogleSignInFailure: ${action.exception}")
        setGoogleLoading(false)
        when {
            message?.contains(A_NETWORK_ERROR) == true -> showError(getString(Res.string.internet_connection_unavailable))
            message?.contains(ID_TOKEN_IS_NULL) == true -> showError(getString(Res.string.sign_in_canceled))
            else -> showError(message)
        }
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

    companion object {
        private const val A_NETWORK_ERROR = "A network error"
        private const val ID_TOKEN_IS_NULL = "Idtoken is null"
    }

}