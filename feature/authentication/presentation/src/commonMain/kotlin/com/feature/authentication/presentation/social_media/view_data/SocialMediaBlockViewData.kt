package com.feature.authentication.presentation.social_media.view_data

import androidx.compose.runtime.Immutable

@Immutable
data class SocialMediaBlockViewData(
    val google: GoogleButtonViewData = GoogleButtonViewData()
)

data class GoogleButtonViewData(
    val startGoogleSignFlow: Boolean = false,
    val isLoading: Boolean = false
)
