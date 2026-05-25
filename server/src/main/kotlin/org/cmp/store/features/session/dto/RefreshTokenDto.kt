package org.cmp.store.features.session.dto

import org.cmp.store.domain.auth.AuthProvider

/** A row of `refresh_tokens`. Never leaves the server — [tokenHash] is not a credential. */
data class RefreshTokenDto(
    val id: String,
    val tokenHash: String,
    val familyId: String,
    val customerId: String,
    val provider: AuthProvider,
    val expiresAt: Long,
    val revokedAt: Long? = null,
    val replacedBy: String? = null,
)
