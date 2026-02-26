package com.feature.authentication.presentation.social_media

import com.store.core.presentation.utils.ViewAction

sealed interface SocialMediaViewAction : ViewAction {
    data object OnGoogleClick : SocialMediaViewAction
    data object OnGoogleSignInFlowError : SocialMediaViewAction
    data class OnReceiveSocialMediaToken(val accessToken: String) : SocialMediaViewAction
}
