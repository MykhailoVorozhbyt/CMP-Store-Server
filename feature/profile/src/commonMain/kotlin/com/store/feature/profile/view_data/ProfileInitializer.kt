package com.store.feature.profile.view_data

import com.store.component.customer.usecase.ReadCustomerUseCase
import com.store.core.domain.ApiResult
import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.utils.ScreenState
import com.store.feature.profile.mappers.toFormViewData
import com.store.feature.profile.validator.ProfileValidator

class ProfileInitializer(
    private val readCustomerUseCase: ReadCustomerUseCase,
    private val validator: ProfileValidator,
) {

    fun initialize(ctx: ActionHandlerScope<ProfileViewData>) {
        ctx.launch {
            ctx.updateViewData { copy(screenState = ScreenState.Loading) }
            when (val result = readCustomerUseCase()) {
                is ApiResult.Success -> {
                    ctx.updateViewData {
                        copy(
                            screenState = ScreenState.Success,
                            form = validator.validateAll(result.data.toFormViewData()),
                        )
                    }
                }

                is ApiResult.Error -> ctx.updateViewData { copy(screenState = ScreenState.Error) }
                ApiResult.NoInternet -> ctx.updateViewData { copy(screenState = ScreenState.NoInternetConnection) }
            }
        }
    }
}
