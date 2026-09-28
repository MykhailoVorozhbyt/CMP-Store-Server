package com.store.component.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val id: String,
    val productId: String,
    val flavor: String? = null,
    val quantity: Int,
)
