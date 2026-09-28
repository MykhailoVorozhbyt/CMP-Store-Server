package com.store.feature.authentication.fakes

import com.store.feature.authentication.handler.SignInFailureHandler
import com.store.component.auth.model.AuthError

/**
 * Test double for [SignInFailureHandler]. Maps failures to deterministic strings without touching
 * Compose resources — `getString()` requires a resource environment that is absent in host-side
 * unit tests (androidHostTest), where it throws and the ShowMessage event is never emitted.
 */
class FakeSignInFailureHandler : SignInFailureHandler {

    override suspend fun handle(exception: Throwable): String =
        exception.message ?: UNKNOWN_ERROR

    override suspend fun handle(error: AuthError): String = error.toString()

    override suspend fun handle(message: String?): String = message ?: UNKNOWN_ERROR

    companion object {
        const val UNKNOWN_ERROR = "unknown error"
    }
}
