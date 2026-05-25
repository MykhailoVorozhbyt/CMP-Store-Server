package com.feature.authentication.domain.model.request

data class AuthUserRequest(
    val uid: String?,
    val displayName: String?,
    val email: String?,
)