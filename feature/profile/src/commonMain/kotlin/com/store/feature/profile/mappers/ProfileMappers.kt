package com.store.feature.profile.mappers

import com.store.feature.profile.view_data.Country
import com.store.feature.profile.view_data.PhoneFieldViewData
import com.store.feature.profile.view_data.ProfileFormViewData
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

internal fun Customer.toFormViewData(): ProfileFormViewData {
    val form = ProfileFormViewData(phone = PhoneFieldViewData(country = Country.fromDialCode(phoneNumber?.dialCode)))
    form.firstName.input = firstName
    form.lastName.input = lastName
    form.email.input = email
    form.city.input = city.orEmpty()
    form.postalCode.input = postalCode?.toString().orEmpty()
    form.address.input = address.orEmpty()
    form.phone.number.input = phoneNumber?.number.orEmpty()
    return form
}

//internal fun ProfileFormViewData.applyTo(customer: Customer): Customer {
//    // TODO(human)
//}
