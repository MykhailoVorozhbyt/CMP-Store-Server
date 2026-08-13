package org.cmp.store.domain.auth.request

import kotlinx.serialization.Serializable
import org.cmp.store.domain.auth.AuthProvider

@Serializable
data class AuthRequest(
    val provider: AuthProvider,
    val email: String,
    val password: String? = null,
    val providerUserId: String? = null,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
)
