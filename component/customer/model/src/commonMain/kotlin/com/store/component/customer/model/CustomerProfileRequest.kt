package com.store.component.customer.model

import org.cmp.store.domain.customer.PhoneNumber

data class CustomerProfileRequest(
    val firstName: String,
    val lastName: String,
    val city: String?,
    val postalCode: Int?,
    val address: String?,
    val phoneNumber: PhoneNumber?,
)
