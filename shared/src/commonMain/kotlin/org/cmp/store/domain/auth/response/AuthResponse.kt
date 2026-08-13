package org.cmp.store.domain.auth.response

import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.customer.Customer

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val customer: Customer,
    val isNewAccount: Boolean,
    val provider: AuthProvider,
)
