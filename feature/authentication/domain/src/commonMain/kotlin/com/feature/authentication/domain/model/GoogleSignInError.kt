package com.feature.authentication.domain.model

import org.cmp.store.network.ApiError

sealed class GoogleSignInError(message: String) : Exception(message), ApiError {

    data class AuthorizationDenied(val reason: String) :
        GoogleSignInError("Google sign-in was denied: $reason")

    class InvalidState :
        GoogleSignInError("Google sign-in failed: invalid state.")

    class MissingAuthorizationCode :
        GoogleSignInError("Google sign-in failed: authorization code is missing.")

    data class TokenExchangeFailed(val statusCode: Int, val body: String) :
        GoogleSignInError("Google token exchange failed with HTTP $statusCode: $body")

    class MissingIdToken :
        GoogleSignInError("Google token exchange failed: id_token is missing.")

    class NonceMismatch :
        GoogleSignInError("Google sign-in failed: nonce mismatch.")

    class DesktopNotSupported :
        GoogleSignInError("Desktop Google sign-in is not supported on this platform.")

    data class ServerStartFailed(override val cause: Throwable) :
        GoogleSignInError("Failed to start local callback server: ${cause.message}")

    class Timeout : GoogleSignInError("Google sign-in timeout exception")

    class InvalidIdToken :
        GoogleSignInError("Invalid Id Token")

    data class Unknown(override val cause: Throwable) :
        GoogleSignInError("Google sign-in failed: ${cause.message}")
}