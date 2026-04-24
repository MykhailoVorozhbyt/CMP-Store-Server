package com.feature.authentication.presentation.social_media

import com.feature.authentication.presentation.social_media.view_data.SocialMediaBlockViewData

object SocialMediaBlockMockPreview {
    fun getViewData() = SocialMediaBlockViewData()

    fun getLoadingViewData(): SocialMediaBlockViewData {
        return getViewData().copy(google = getViewData().google.copy(isLoading = true))
    }
}