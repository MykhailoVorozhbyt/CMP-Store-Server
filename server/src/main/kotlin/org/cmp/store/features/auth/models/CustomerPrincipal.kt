package org.cmp.store.features.auth.models

import org.cmp.store.domain.auth.AuthProvider

/**
 * The authenticated caller. [accessToken] rides along so routes acting on the session itself —
 * logout — can tell which of the customer's logins made the call.
 *
 * Not `@Serializable`, and outside `dto` on purpose: it holds a live bearer token, and what
 * keeps that from leaking is that `call.respond(principal)` must not compile.
 */
data class CustomerPrincipal(
    val customerId: String,
    val provider: AuthProvider,
    val accessToken: String,
)
