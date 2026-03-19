package com.feature.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feature.authentication.presentation.social_media.SocialMediaViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.presentation.ui.ViewAction
import com.store.core.utils.Logger
import com.store.core.utils.i
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationViewModel() : ViewModel() {
    private val _viewData = MutableStateFlow(AuthenticationViewData())
    val viewData: StateFlow<AuthenticationViewData> = _viewData

    fun onViewAction(viewAction: ViewAction) {
        when (viewAction) {
            SocialMediaViewAction.OnGoogleClick -> {
                viewModelScope.launch {
                    setGoogleLoading(true)
                    delay(5000L)
                    setGoogleLoading(false)
                }
            }

            is SocialMediaViewAction.OnGoogleSignInFailure -> {
                val message = viewAction.exception.message
                Logger.i("OnGoogleSignInFailure: ${viewAction.exception}")
                if (message?.contains("A network error") == true) {
//                    messageBarState.addError("Internet connection unavailable.")
                } else if (message?.contains("Idtoken is null") == true) {
//                    messageBarState.addError("Sign in canceled.")
                } else {
//                    messageBarState.addError(message?: "Unknown")
                }
                setGoogleLoading(false)
            }

            is SocialMediaViewAction.OnGoogleSignInSuccess -> {
                Logger.i("OnGoogleSignInSuccess: ${viewAction.user}")
//                    viewModel.createCustomer(
//                        user = user,
//                        onSuccess = {
//                            scope.launch {
//                                messageBarState.addSuccess("Authentication successful!")
//                                delay(2000)
//                                navigateToHome()
//                            }
//                        },
//                        onError = { message -> messageBarState.addError(message) }
//                    )
                setGoogleLoading(false)
            }
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

}