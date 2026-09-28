package com.store.component.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PhoneNumberDto(
    val dialCode: Int,
    val number: String,
)
