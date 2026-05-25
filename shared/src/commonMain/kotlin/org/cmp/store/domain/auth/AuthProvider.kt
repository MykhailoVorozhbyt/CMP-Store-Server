package org.cmp.store.domain.auth

import kotlinx.serialization.Serializable

@Serializable
enum class AuthProvider {
    MANUAL,
    GOOGLE,
    FACEBOOK
}
