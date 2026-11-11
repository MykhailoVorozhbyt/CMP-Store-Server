package com.store.feature.profile.view_data

import com.store.component.customer.model.CustomerProfileRequest
import com.store.component.customer.usecase.GetCachedCustomerUseCase
import com.store.component.customer.usecase.ReadCustomerUseCase
import com.store.core.domain.ApiResult
import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.utils.ScreenState
import com.store.feature.profile.mappers.toFormViewData
import com.store.feature.profile.mappers.toProfileRequest
import com.store.feature.profile.validator.ProfileValidator
import org.cmp.store.domain.customer.Customer

class ProfileInitializer(
    private val readCustomerUseCase: ReadCustomerUseCase,
    private val getCachedCustomerUseCase: GetCachedCustomerUseCase,
    private val validator: ProfileValidator,
) {

    fun initialize(
        ctx: ActionHandlerScope<ProfileViewData>,
        onProfileLoaded: (CustomerProfileRequest) -> Unit,
    ) {
        ctx.launch {
            val cached = getCachedCustomerUseCase()
            if (cached != null) {
                showCustomer(ctx, cached, onProfileLoaded)
            } else {
                ctx.updateViewData { copy(screenState = ScreenState.Loading) }
            }
            when (val result = readCustomerUseCase()) {
                is ApiResult.Success -> if (result.data != cached) showCustomer(ctx, result.data, onProfileLoaded)
                is ApiResult.Error -> if (cached == null) ctx.updateViewData { copy(screenState = ScreenState.Error) }
                ApiResult.NoInternet -> if (cached == null) {
                    ctx.updateViewData { copy(screenState = ScreenState.NoInternetConnection) }
                }
            }
        }
    }

    private fun showCustomer(
        ctx: ActionHandlerScope<ProfileViewData>,
        customer: Customer,
        onProfileLoaded: (CustomerProfileRequest) -> Unit,
    ) {
        val form = validator.validateAll(customer.toFormViewData())
        onProfileLoaded(form.toProfileRequest())
        ctx.updateViewData { copy(screenState = ScreenState.Success, form = form) }
    }
}
