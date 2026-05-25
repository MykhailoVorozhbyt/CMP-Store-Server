package com.feature.authentication.presentation.social_media.view_data

import androidx.compose.runtime.Immutable

@Immutable
data class SocialMediaBlockViewData(
    val google: SocialMediaButtonViewData = SocialMediaButtonViewData()
)

data class SocialMediaButtonViewData(
    val isLoading: Boolean = false
)
