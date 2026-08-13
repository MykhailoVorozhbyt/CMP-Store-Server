package com.feature.authentication.presentation.fakes

import com.feature.authentication.presentation.handler.SignInFailureHandler
import org.cmp.store.network.NetworkError

/**
 * Test double for [SignInFailureHandler]. Maps failures to deterministic strings without touching
 * Compose resources — `getString()` requires a resource environment that is absent in host-side
 * unit tests (androidHostTest), where it throws and the ShowMessage event is never emitted.
 */
class FakeSignInFailureHandler : SignInFailureHandler {

    override suspend fun handle(exception: Throwable): String =
        exception.message ?: UNKNOWN_ERROR

    override suspend fun handle(error: NetworkError): String = error.name

    override suspend fun handle(message: String?): String = message ?: UNKNOWN_ERROR

    companion object {
        const val UNKNOWN_ERROR = "unknown error"
    }
}
