package com.store.feature.profile

import com.store.core.presentation.utils.NamedPreviewParameter
import com.store.core.presentation.utils.PreviewProvider
import com.store.core.presentation.utils.ScreenState
import com.store.feature.profile.mappers.toFormViewData
import com.store.feature.profile.validator.ProfileValidator
import com.store.feature.profile.view_data.Country
import com.store.feature.profile.view_data.CountryPickerViewData
import com.store.feature.profile.view_data.ProfileViewData
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

class ProfilePreviewProvider : PreviewProvider<ProfileViewData>() {

    override val parameters: List<NamedPreviewParameter<ProfileViewData>>
        get() = listOf(
            NamedPreviewParameter("Loading", ProfileViewData()),
            NamedPreviewParameter("Error", ProfileViewData(screenState = ScreenState.Error)),
            NamedPreviewParameter("No internet", ProfileViewData(screenState = ScreenState.NoInternetConnection)),
            NamedPreviewParameter("Content", content(customer)),
            NamedPreviewParameter("Invalid city", content(customer.copy(city = "Ky"))),
            NamedPreviewParameter("Country picker", content(customer).copy(countryPicker = CountryPickerViewData(selected = Country.Ukraine))),
            NamedPreviewParameter("Dial code not found", content(customer).copy(countryPicker = notFoundPicker())),
        )

    private fun content(customer: Customer) = ProfileViewData(
        screenState = ScreenState.Success,
        form = ProfileValidator().validateAll(customer.toFormViewData()),
    )

    private fun notFoundPicker() = CountryPickerViewData().also { it.query.input = "0001" }

    private companion object {
        val customer = Customer(
            id = "preview",
            firstName = "Stefan",
            lastName = "Jovanovic",
            email = "stefan.jovanovic@gmail.com",
            city = "Kyiv",
            postalCode = 11000,
            address = "Khreshchatyk 11",
            phoneNumber = PhoneNumber(dialCode = 380, number = "123456789"),
        )
    }
}
