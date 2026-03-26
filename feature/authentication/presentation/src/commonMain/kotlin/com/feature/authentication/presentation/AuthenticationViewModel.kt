package com.feature.authentication.presentation

import androidx.lifecycle.viewModelScope
import com.feature.authentication.presentation.social_media.SocialMediaViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import com.store.core.utils.Logger
import com.store.core.utils.i
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationViewModel(
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
            delay(5000L)
            setGoogleLoading(false)
        }
    }

    private fun handleGoogleSignInSuccess(action: SocialMediaViewAction.OnGoogleSignInSuccess) {
        Logger.i("OnGoogleSignInSuccess: ${action.user}")
//        createCustomer(
//            user = user,
//            onSuccess = {
//                scope.launch {
//                    showSuccess("Authentication successful!")
//                    delay(2000)
////                    navigateToHome()
//                }
//            },
//            onError = { message -> messageBarState.addError(message) }
//        )
        setGoogleLoading(false)
    }

    private fun handleGoogleSignInFailure(action: SocialMediaViewAction.OnGoogleSignInFailure) {
        val message = action.exception.message
        Logger.i("OnGoogleSignInFailure: ${action.exception}")
        when {
            message?.contains("A network error") == true -> showError("Internet connection unavailable.")
            message?.contains("Idtoken is null") == true -> showError("Sign in canceled.")
            else -> showError(message ?: "Unknown")
        }
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

}