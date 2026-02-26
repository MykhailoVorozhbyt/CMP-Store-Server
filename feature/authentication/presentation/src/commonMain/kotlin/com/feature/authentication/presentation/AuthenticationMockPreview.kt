package com.feature.authentication.presentation

import com.feature.authentication.presentation.social_media.SocialMediaBlockMockPreview
import com.feature.authentication.presentation.view_data.AuthenticationViewData

object AuthenticationMockPreview {
    fun getViewData() = AuthenticationViewData(
        socialMedia = SocialMediaBlockMockPreview.getViewData()
    )
}