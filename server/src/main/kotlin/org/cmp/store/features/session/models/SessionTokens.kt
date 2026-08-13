package org.cmp.store.features.session.models

/** The pair handed to a client on login and on every rotation. */
data class SessionTokens(
    val accessToken: String,
    val refreshToken: String,
)