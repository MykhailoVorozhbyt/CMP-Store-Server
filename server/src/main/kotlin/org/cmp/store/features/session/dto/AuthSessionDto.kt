package org.cmp.store.features.session.dto

import org.cmp.store.domain.auth.AuthProvider

data class AuthSessionDto(
    val customerId: String,
    val provider: AuthProvider,
    val expiresAt: Long,
    val familyId: String,
)
