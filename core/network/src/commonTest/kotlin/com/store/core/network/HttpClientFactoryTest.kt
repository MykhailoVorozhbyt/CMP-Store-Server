package com.store.core.network

import com.store.test.fakes.FakeLocalAuthSessionDataSource
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import kotlinx.coroutines.test.runTest
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Covers the one behaviour nothing above the client can see: a request that fails on an expired
 * access token quietly becomes three requests, and the caller only ever learns about the last.
 */
class HttpClientFactoryTest {

    private val session = FakeLocalAuthSessionDataSource()

    @Test
    fun an_expired_access_token_is_rotated_and_the_original_request_is_replayed() = runTest {
        session.accessToken = "stale-access"
        session.refreshToken = "live-refresh"
        val requests = mutableListOf<HttpRequestData>()
        val client = createHttpClient(
            session,
            MockEngine { request ->
                requests += request
                when {
                    request.url.fullPath.endsWith(REFRESH_ROUTE) ->
                        respondJson(tokensJson("fresh-access", "fresh-refresh"))

                    request.headers[HttpHeaders.Authorization] == "Bearer fresh-access" ->
                        respondJson("""{"ok":true}""")

                    else -> respondError(HttpStatusCode.Unauthorized)
                }
            }
        )

        client.get(PROTECTED_ROUTE)

        assertEquals(3, requests.size)
        assertEquals("Bearer stale-access", requests[0].headers[HttpHeaders.Authorization])
        assertTrue(requests[1].url.fullPath.endsWith(REFRESH_ROUTE))
        assertEquals("Bearer fresh-access", requests[2].headers[HttpHeaders.Authorization])
        // Storing the rotated pair is what keeps the next cold start signed in — the old
        // refresh token is already spent server-side by now.
        assertEquals("fresh-access", session.accessToken)
        assertEquals("fresh-refresh", session.refreshToken)
    }

    @Test
    fun a_rejected_refresh_clears_the_session_and_surfaces_the_failure() = runTest {
        session.userId = "uid"
        session.accessToken = "stale-access"
        session.refreshToken = "dead-refresh"
        val client = createHttpClient(
            session,
            MockEngine { respondError(HttpStatusCode.Unauthorized) }
        )

        assertFailsWith<Exception> { client.get(PROTECTED_ROUTE) }

        assertTrue(session.signOutCalled)
        assertNull(session.userId)
        assertNull(session.accessToken)
        assertNull(session.refreshToken)
    }

    @Test
    fun a_throttled_refresh_leaves_the_stored_session_alone() = runTest {
        // The auth bucket is small and shared by everyone behind one address, so a 429 here
        // says nothing about this token. Signing out on it would destroy a week-long session
        // because the server was busy for a minute.
        session.userId = "uid"
        session.accessToken = "stale-access"
        session.refreshToken = "live-refresh"
        val client = createHttpClient(
            session,
            MockEngine { request ->
                if (request.url.fullPath.endsWith(REFRESH_ROUTE)) {
                    respondError(HttpStatusCode.TooManyRequests)
                } else {
                    respondError(HttpStatusCode.Unauthorized)
                }
            }
        )

        assertFailsWith<Exception> { client.get(PROTECTED_ROUTE) }

        assertFalse(session.signOutCalled)
        assertEquals("uid", session.userId)
        assertEquals("live-refresh", session.refreshToken)
    }

    @Test
    fun a_server_error_during_refresh_leaves_the_stored_session_alone() = runTest {
        session.userId = "uid"
        session.accessToken = "stale-access"
        session.refreshToken = "live-refresh"
        val client = createHttpClient(
            session,
            MockEngine { request ->
                if (request.url.fullPath.endsWith(REFRESH_ROUTE)) {
                    respondError(HttpStatusCode.ServiceUnavailable)
                } else {
                    respondError(HttpStatusCode.Unauthorized)
                }
            }
        )

        assertFailsWith<Exception> { client.get(PROTECTED_ROUTE) }

        assertFalse(session.signOutCalled)
        assertEquals("live-refresh", session.refreshToken)
    }

    @Test
    fun a_refresh_token_the_server_calls_dead_clears_the_session() = runTest {
        session.userId = "uid"
        session.accessToken = "stale-access"
        session.refreshToken = "dead-refresh"
        val client = createHttpClient(
            session,
            MockEngine { request ->
                if (request.url.fullPath.endsWith(REFRESH_ROUTE)) {
                    respondJson(
                        body = NetworkError.INVALID_REFRESH_TOKEN.name,
                        status = HttpStatusCode.Unauthorized,
                    )
                } else {
                    respondError(HttpStatusCode.Unauthorized)
                }
            }
        )

        assertFailsWith<Exception> { client.get(PROTECTED_ROUTE) }

        assertTrue(session.signOutCalled)
        assertNull(session.refreshToken)
    }

    @Test
    fun a_rejected_refresh_is_attempted_once_rather_than_looping() = runTest {
        // The refresh call carries AuthCircuitBreaker precisely so its own 401 does not feed
        // back into the plugin. Without it this test never finishes.
        session.accessToken = "stale-access"
        session.refreshToken = "dead-refresh"
        var refreshAttempts = 0
        val client = createHttpClient(
            session,
            MockEngine { request ->
                if (request.url.fullPath.endsWith(REFRESH_ROUTE)) refreshAttempts++
                respondError(HttpStatusCode.Unauthorized)
            }
        )

        assertFailsWith<Exception> { client.get(PROTECTED_ROUTE) }

        assertEquals(1, refreshAttempts)
    }

    @Test
    fun a_request_made_without_a_stored_session_carries_no_authorization_header() = runTest {
        var request: HttpRequestData? = null
        val client = createHttpClient(
            session,
            MockEngine {
                request = it
                respondJson("""{"ok":true}""")
            }
        )

        client.get(PUBLIC_ROUTE)

        assertNull(request?.headers?.get(HttpHeaders.Authorization))
    }

    private companion object {
        const val REFRESH_ROUTE = "/auth/refresh"
        const val PROTECTED_ROUTE = "customer/uid"
        const val PUBLIC_ROUTE = "product/new"
    }
}