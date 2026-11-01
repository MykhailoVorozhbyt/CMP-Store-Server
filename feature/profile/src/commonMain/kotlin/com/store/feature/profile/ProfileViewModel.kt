package com.store.feature.profile

import com.store.component.customer.usecase.UpdateCustomerUseCase
import com.store.core.domain.ApiResult
import com.store.core.presentation.core.di.coroutines.AppDispatchers
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.feature.profile.handler.ProfileMessageHandler
import com.store.feature.profile.mappers.toProfileRequest
import com.store.feature.profile.validator.ProfileValidator
import com.store.feature.profile.view_data.CountryPickerViewData
import com.store.feature.profile.view_data.ProfileFields
import com.store.feature.profile.view_data.ProfileInitializer
import com.store.feature.profile.view_data.ProfileViewAction
import com.store.feature.profile.view_data.ProfileViewData
import kotlinx.coroutines.flow.MutableStateFlow

class ProfileViewModel(
    initializer: ProfileInitializer,
    private val updateCustomerUseCase: UpdateCustomerUseCase,
    private val validator: ProfileValidator,
    private val messageHandler: ProfileMessageHandler,
    dispatchers: AppDispatchers,
) : BaseActionHandleViewModel<ProfileViewData>(dispatchers) {
    override val _viewData = MutableStateFlow(ProfileViewData())

    init {
        initializer.initialize(this)
    }

    override suspend fun handleViewAction(action: ViewAction) {
        when (action) {
            is InputFieldChanged -> handleInputChanged(action)
            ProfileViewAction.UpdateClicked -> handleUpdateClicked()
            ProfileViewAction.CountryPickerClicked -> handleCountryPickerClicked()
            is ProfileViewAction.CountryHighlighted -> handleCountryHighlighted(action)
            ProfileViewAction.CountryConfirmed -> handleCountryConfirmed()
            ProfileViewAction.CountryPickerDismissed -> updateViewData { copy(countryPicker = null) }
        }
    }

    private fun handleInputChanged(action: InputFieldChanged) {
        if (action.field == ProfileFields.DialCodeSearch) {
            viewData.countryPicker?.query?.input = action.value
        } else {
            validator.validate(action, this)
        }
    }

    private fun handleUpdateClicked() {
        if (!viewData.updateEnabled || viewData.isUpdating) return
        launchIo {
            updateViewData { copy(isUpdating = true) }
            when (val result = updateCustomerUseCase(viewData.form.toProfileRequest())) {
                is ApiResult.Success -> showSuccess(messageHandler.updateSuccess())
                is ApiResult.Error -> showError(messageHandler.handle(result.error))
                ApiResult.NoInternet -> showError(messageHandler.noInternet())
            }
            updateViewData { copy(isUpdating = false) }
        }
    }

    private fun handleCountryPickerClicked() {
        updateViewData { copy(countryPicker = CountryPickerViewData(selected = form.phone.country)) }
    }

    private fun handleCountryHighlighted(action: ProfileViewAction.CountryHighlighted) {
        updateViewData { copy(countryPicker = countryPicker?.copy(selected = action.country)) }
    }

    private fun handleCountryConfirmed() {
        val picker = viewData.countryPicker ?: return
        val selected = picker.selected?.takeIf { picker.confirmEnabled } ?: return
        updateViewData {
            copy(
                form = form.copy(phone = form.phone.copy(country = selected)),
                countryPicker = null
            )
        }
    }
}
