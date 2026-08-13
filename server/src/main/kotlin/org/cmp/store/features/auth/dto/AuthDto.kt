package org.cmp.store.features.auth.dto

import kotlinx.serialization.Serializable
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.customer.dto.CustomerDto

@Serializable
data class AuthRequestDto(
    val provider: AuthProvider,
    val email: String,
    val password: String? = null,
    val providerUserId: String? = null,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
)

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val customer: CustomerDto,
    val isNewAccount: Boolean,
    val provider: AuthProvider,
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String,
)

/**
 * Rotation returns only the new pair — the customer is unchanged and the client already
 * holds it, so re-sending it on every hourly refresh would be pure waste.
 */
@Serializable
data class SessionTokensDto(
    val accessToken: String,
    val refreshToken: String,
)