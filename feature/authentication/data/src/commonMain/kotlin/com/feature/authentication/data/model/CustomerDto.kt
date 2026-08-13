package com.feature.authentication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String? = null,
    val postalCode: Int? = null,
    val address: String? = null,
    val phoneNumber: PhoneNumberDto? = null,
    val cart: List<CartItemDto> = emptyList(),
    val isAdmin: Boolean = false,
)
