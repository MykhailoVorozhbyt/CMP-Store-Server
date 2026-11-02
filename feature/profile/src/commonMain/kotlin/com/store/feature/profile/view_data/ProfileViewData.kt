package com.store.feature.profile.view_data

import androidx.compose.runtime.Immutable
import com.store.core.presentation.ui.base.view_data.InputFieldConfig
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.ScreenState
import com.store.core.presentation.utils.intRegexRules
import com.store.core.presentation.utils.isSuccess

@Immutable
data class ProfileViewData(
    val screenState: ScreenState = ScreenState.Loading,
    val form: ProfileFormViewData = ProfileFormViewData(),
    val isUpdating: Boolean = false,
    val countryPicker: CountryPickerViewData? = null,
) {
    val updateEnabled: Boolean get() = screenState.isSuccess() && !isUpdating
}

@Immutable
data class ProfileFormViewData(
    val firstName: InputFieldViewData = InputFieldViewData(field = ProfileFields.FirstName),
    val lastName: InputFieldViewData = InputFieldViewData(field = ProfileFields.LastName),
    val email: InputFieldViewData = InputFieldViewData(field = ProfileFields.Email, enabled = false),
    val city: InputFieldViewData = InputFieldViewData(field = ProfileFields.City),
    val postalCode: InputFieldViewData = InputFieldViewData(
        field = ProfileFields.PostalCode,
        config = InputFieldConfig(regex = intRegexRules),
    ),
    val address: InputFieldViewData = InputFieldViewData(field = ProfileFields.Address),
    val phone: PhoneFieldViewData = PhoneFieldViewData(),
) {
    private val editableFields: List<InputFieldViewData>
        get() = listOf(firstName, lastName, city, postalCode, address, phone.number)

    val isValid: Boolean get() = editableFields.all { it.isAllValid }
}

@Immutable
data class CountryPickerViewData(
    val query: InputFieldViewData = InputFieldViewData(
        field = ProfileFields.DialCodeSearch,
        config = InputFieldConfig(regex = intRegexRules),
    ),
    val selected: Country? = null,
) {
    val countries: List<Country> get() = Country.search(query.input)
    val confirmEnabled: Boolean get() = selected != null && selected in countries
}
