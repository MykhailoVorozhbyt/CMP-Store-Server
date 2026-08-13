package org.cmp.store.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredential(
    val id: String,
    val customerId: String,
    val provider: AuthProvider,
    val providerUserId: String? = null,
    val email: String,
    val passwordHash: String? = null,
    val isVerified: Boolean = false,
    val createdAt: Long,
)
