package com.feature.authentication.presentation.view_data

import com.store.core.presentation.ui.base.ViewDataInitializer
import com.store.core.presentation.ui.base.view_data.InputFieldConfig
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.emailRegexRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class AuthenticationInitializer : ViewDataInitializer.StateProduce<AuthenticationViewData> {

    override fun initialize(viewDataState: MutableStateFlow<AuthenticationViewData>) {
        viewDataState.update {
            buildInitial()
        }
    }

    private fun buildInitial(): AuthenticationViewData {
        return AuthenticationViewData(
            isLoading = false,
            manual = ManualBlockViewData(
                email = InputFieldViewData(
                    field = AuthenticationFields.Email,
                    config = InputFieldConfig(regex = emailRegexRule)
                ),
                password = InputFieldViewData(
                    field = AuthenticationFields.Password
                ),
            ),
        )
    }
}