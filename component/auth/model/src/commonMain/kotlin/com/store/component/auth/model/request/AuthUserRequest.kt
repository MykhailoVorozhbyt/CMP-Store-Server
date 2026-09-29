package com.store.component.auth.model.request

data class AuthUserRequest(
    val uid: String?,
    val displayName: String?,
    val email: String?,
)