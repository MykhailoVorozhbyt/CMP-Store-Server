package com.feature.authentication.data.model

import kotlinx.serialization.Serializable
import org.cmp.store.domain.auth.AuthProvider

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val customer: CustomerDto,
    val isNewAccount: Boolean,
    val provider: AuthProvider,
)
