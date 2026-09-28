package com.store.component.auth.data.mappers

import com.store.component.auth.model.AuthError
import com.store.core.network.utils.toDataError
import org.cmp.store.network.NetworkError

internal fun NetworkError.toAuthError(): AuthError = when (this) {
    NetworkError.INVALID_CREDENTIALS -> AuthError.InvalidCredentials
    NetworkError.ACCOUNT_HAS_NO_PASSWORD -> AuthError.AccountHasNoPassword
    NetworkError.USER_ALREADY_EXISTS -> AuthError.UserAlreadyExists
    NetworkError.EMAIL_REQUIRED -> AuthError.EmailRequired
    NetworkError.PASSWORD_REQUIRED -> AuthError.PasswordRequired
    NetworkError.PROVIDER_USER_ID_REQUIRED -> AuthError.ProviderUserIdRequired
    NetworkError.AUTH_PROVIDER_NOT_SUPPORTED -> AuthError.ProviderNotSupported
    else -> AuthError.Common(toDataError())
}
