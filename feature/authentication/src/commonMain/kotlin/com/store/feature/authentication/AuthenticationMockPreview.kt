package com.store.feature.authentication

import com.store.feature.authentication.social_media.SocialMediaBlockMockPreview
import com.store.feature.authentication.view_data.AuthenticationViewData

object AuthenticationMockPreview {
    fun getViewData() = AuthenticationViewData(
        socialMedia = SocialMediaBlockMockPreview.getViewData()
    )
}