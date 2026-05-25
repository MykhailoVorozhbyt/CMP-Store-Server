package com.feature.authentication.presentation.social_media

import com.feature.authentication.domain.model.GoogleSignInError
import com.store.core.presentation.ui.ViewAction
import dev.gitlive.firebase.auth.FirebaseUser

sealed interface SocialMediaViewAction : ViewAction {
    data object OnGoogleClick : SocialMediaViewAction
    data class OnSignInFailure(val exception: GoogleSignInError) : SocialMediaViewAction
    data class OnSignInSuccess(val user: FirebaseUser?) : SocialMediaViewAction
}
