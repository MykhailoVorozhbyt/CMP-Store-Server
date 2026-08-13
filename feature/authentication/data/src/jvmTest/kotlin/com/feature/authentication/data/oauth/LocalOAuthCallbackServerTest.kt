package com.feature.authentication.data.oauth

import com.feature.authentication.data.model.AuthCallback
import com.feature.authentication.domain.model.GoogleSignInError
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class LocalOAuthCallbackServerTest {

    private val client = HttpClient(OkHttp)

    @AfterTest
    fun tearDown() {
        client.close()
    }

    private fun hitCallback(port: Int, query: String) = runBlocking {
        client.get("http://localhost:$port/callback?$query")
    }

    @Test
    fun createCallback_with_matching_state_and_code_completes_future_with_AuthCallback() {
        val callback = LocalOAuthCallbackServer().createCallback(expectedState = "expected-state")
        callback.start()

        try {
            hitCallback(callback.port, "state=expected-state&code=auth-code-123")

            val result = callback.callbackFuture.get(5, TimeUnit.SECONDS)

            assertEquals(AuthCallback(code = "auth-code-123"), result)
        } finally {
            callback.stop()
        }
    }

    @Test
    fun createCallback_with_mismatched_state_completes_future_exceptionally_with_InvalidState() {
        val callback = LocalOAuthCallbackServer().createCallback(expectedState = "expected-state")
        callback.start()

        try {
            hitCallback(callback.port, "state=wrong-state&code=auth-code-123")

            val exception = assertFailsWith<ExecutionException> {
                callback.callbackFuture.get(5, TimeUnit.SECONDS)
            }

            assertIs<GoogleSignInError.InvalidState>(exception.cause)
        } finally {
            callback.stop()
        }
    }
}