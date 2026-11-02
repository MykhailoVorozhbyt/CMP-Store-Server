package org.cmp.store.features.customer.dto

import kotlinx.serialization.Serializable

@Serializable
data class CustomerProfileDto(
    val firstName: String,
    val lastName: String,
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val phoneNumber: PhoneNumberDto? = null,
)
