package com.store.feature.authentication.social_media

import com.store.feature.authentication.social_media.view_data.SocialMediaBlockViewData

object SocialMediaBlockMockPreview {
    fun getViewData() = SocialMediaBlockViewData()

    fun getLoadingViewData(): SocialMediaBlockViewData {
        return getViewData().copy(google = getViewData().google.copy(isLoading = true))
    }
}