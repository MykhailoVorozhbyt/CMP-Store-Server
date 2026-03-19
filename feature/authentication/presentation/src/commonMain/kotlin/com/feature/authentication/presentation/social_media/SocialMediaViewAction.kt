package com.feature.authentication.presentation.social_media

import com.store.core.presentation.ui.ViewAction
import dev.gitlive.firebase.auth.FirebaseUser

sealed interface SocialMediaViewAction : ViewAction {
    data object OnGoogleClick : SocialMediaViewAction
    data class OnGoogleSignInFailure(val exception: Throwable): SocialMediaViewAction
    data class OnGoogleSignInSuccess(val user: FirebaseUser?) : SocialMediaViewAction
}
