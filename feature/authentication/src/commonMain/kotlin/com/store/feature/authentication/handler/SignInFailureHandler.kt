package com.store.feature.authentication.handler

import com.store.component.auth.model.AuthError
import com.store.component.auth.model.GoogleSignInError
import com.store.core.domain.DataError
import com.store.core.resources.Res
import com.store.core.resources.auth_error_account_has_no_password
import com.store.core.resources.auth_error_canceled
import com.store.core.resources.auth_error_invalid_credentials
import com.store.core.resources.auth_google_signin_error_denied
import com.store.core.resources.auth_google_signin_error_invalid_id_token
import com.store.core.resources.auth_google_signin_error_invalid_state
import com.store.core.resources.auth_google_signin_error_missing_code
import com.store.core.resources.auth_google_signin_error_nonce_mismatch
import com.store.core.resources.auth_google_signin_error_server
import com.store.core.resources.auth_google_signin_error_token_exchange
import com.store.core.resources.auth_google_signin_error_unsupported
import com.store.core.resources.common_error_no_internet
import com.store.core.resources.common_error_unknown
import org.jetbrains.compose.resources.getString

interface SignInFailureHandler {
    suspend fun handle(exception: Throwable): String
    suspend fun handle(error: AuthError): String
    suspend fun handle(message: String?): String
}

class SignInFailureHandlerImpl : SignInFailureHandler {

    override suspend fun handle(exception: Throwable): String {
        return if (exception is GoogleSignInError) {
            handleGoogleSignInError(exception)
        } else {
            handle(exception.message)
        }
    }

    override suspend fun handle(error: AuthError): String = when (error) {
        AuthError.InvalidCredentials -> getString(Res.string.auth_error_invalid_credentials)
        AuthError.AccountHasNoPassword -> getString(Res.string.auth_error_account_has_no_password)
        is AuthError.Common -> when (error.error) {
            DataError.NoInternet -> getString(Res.string.common_error_no_internet)
            else -> getString(Res.string.common_error_unknown)
        }
        AuthError.UserAlreadyExists,
        AuthError.EmailRequired,
        AuthError.PasswordRequired,
        AuthError.ProviderUserIdRequired,
        AuthError.ProviderNotSupported -> getString(Res.string.common_error_unknown)
    }

    override suspend fun handle(message: String?): String {
        return when {
            message?.contains(A_NETWORK_ERROR) == true -> getString(Res.string.common_error_no_internet)
            message?.contains(ID_TOKEN_IS_NULL) == true -> getString(Res.string.auth_error_canceled)
            else -> message ?: getString(Res.string.common_error_unknown)
        }
    }

    private suspend fun handleGoogleSignInError(error: GoogleSignInError?): String {
        return when (error) {
            is GoogleSignInError.AuthorizationDenied -> getString(Res.string.auth_google_signin_error_denied)
            is GoogleSignInError.InvalidState -> getString(Res.string.auth_google_signin_error_invalid_state)
            is GoogleSignInError.MissingAuthorizationCode -> getString(Res.string.auth_google_signin_error_missing_code)
            is GoogleSignInError.TokenExchangeFailed -> getString(Res.string.auth_google_signin_error_token_exchange)
            is GoogleSignInError.MissingIdToken -> getString(Res.string.auth_google_signin_error_server)
            is GoogleSignInError.NonceMismatch -> getString(Res.string.auth_google_signin_error_nonce_mismatch)
            is GoogleSignInError.DesktopNotSupported -> getString(Res.string.auth_google_signin_error_unsupported)
            is GoogleSignInError.ServerStartFailed -> getString(Res.string.auth_google_signin_error_server)
            is GoogleSignInError.InvalidIdToken -> getString(Res.string.auth_google_signin_error_invalid_id_token)
            else -> error?.message ?: getString(Res.string.common_error_unknown)
        }
    }

    companion object {
        private const val A_NETWORK_ERROR = "A network error"
        private const val ID_TOKEN_IS_NULL = "Idtoken is null"
    }
}