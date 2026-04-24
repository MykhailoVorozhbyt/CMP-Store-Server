package com.feature.authentication.presentation.view_data

import com.feature.authentication.presentation.social_media.view_data.SocialMediaBlockViewData

data class AuthenticationViewData(
    val isLoading: Boolean = false,
    val socialMedia: SocialMediaBlockViewData = SocialMediaBlockViewData()
)