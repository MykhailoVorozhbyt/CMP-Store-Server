package com.store.component.auth.model

import com.store.core.domain.DataError

sealed interface AuthError {
    data object InvalidCredentials : AuthError
    data object AccountHasNoPassword : AuthError
    data object UserAlreadyExists : AuthError
    data object EmailRequired : AuthError
    data object PasswordRequired : AuthError
    data object ProviderUserIdRequired : AuthError
    data object ProviderNotSupported : AuthError
    data class Common(val error: DataError) : AuthError
}
