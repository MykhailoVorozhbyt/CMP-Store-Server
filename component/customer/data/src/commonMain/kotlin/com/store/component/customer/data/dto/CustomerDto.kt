package com.store.component.customer.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class CustomerDto(
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

@Serializable
internal data class PhoneNumberDto(
    val dialCode: Int,
    val number: String,
)

@Serializable
internal data class CartItemDto(
    val id: String,
    val productId: String,
    val flavor: String? = null,
    val quantity: Int,
)
