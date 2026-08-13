package com.feature.authentication.presentation.view_data

import com.feature.authentication.presentation.social_media.view_data.SocialMediaBlockViewData
import com.store.core.presentation.ui.base.view_data.InputFieldViewData

data class AuthenticationViewData(
    val isLoading: Boolean = false,
    val manual: ManualBlockViewData = ManualBlockViewData(),
    val socialMedia: SocialMediaBlockViewData = SocialMediaBlockViewData()
)

data class ManualBlockViewData(
    val email: InputFieldViewData = InputFieldViewData(),
    val password: InputFieldViewData = InputFieldViewData()
){
    val buttonEnabled get() = email.isAllValid && password.isAllValid
}