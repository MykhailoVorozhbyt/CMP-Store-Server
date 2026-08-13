package org.cmp.store.features.customer.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val id: String,
    val productId: String,
    val flavor: String? = null,
    val quantity: Int,
)
