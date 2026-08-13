package org.cmp.store.features

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.auth.dto.AuthRequestDto
import org.cmp.store.features.auth.dto.AuthResponseDto
import org.cmp.store.features.auth.dto.RefreshRequestDto
import org.cmp.store.features.auth.dto.SessionTokensDto
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.decodeJson
import org.cmp.store.utils.testJson
import org.cmp.store.utils.testServerApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Route-level cover for `/auth/refresh` and `/auth/logout`. The rotation rules themselves —
 * grace window, reuse detection, family teardown — are exercised against the clock in
 * `AuthSessionServiceTest`; what is only visible here is the HTTP wiring: which status codes
 * come back, and whether a token actually stops opening protected routes.
 */
class SessionRoutesIntegrationTest {

    @Test
    fun refresh_hands_back_a_new_pair() = testServerApplication {
        val session = signIn("refresh-ok@example.com")

        val response = client.post(REFRESH_ROUTE) {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(RefreshRequestDto(session.refreshToken)))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val tokens = response.decodeJson<SessionTokensDto>()
        assertTrue(tokens.accessToken.isNotBlank())
        assertNotEquals(session.accessToken, tokens.accessToken)
        assertNotEquals(session.refreshToken, tokens.refreshToken)
    }

    @Test
    fun the_rotated_access_token_opens_protected_routes() = testServerApplication {
        val session = signIn("refresh-usable@example.com")

        val tokens = client.post(REFRESH_ROUTE) {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(RefreshRequestDto(session.refreshToken)))
        }.decodeJson<SessionTokensDto>()

        val response = client.get("/customer/${session.customerId}") {
            header(HttpHeaders.Authorization, "Bearer ${tokens.accessToken}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun refresh_with_an_unknown_token_is_rejected() = testServerApplication {
        val response = client.post(REFRESH_ROUTE) {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(RefreshRequestDto("never-issued")))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(NetworkError.INVALID_REFRESH_TOKEN.name, response.bodyAsText().trim())
    }

    @Test
    fun logout_without_a_token_is_rejected() = testServerApplication {
        val response = client.post(LOGOUT_ROUTE)

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun logout_with_a_garbage_token_is_rejected() = testServerApplication {
        val response = client.post(LOGOUT_ROUTE) {
            header(HttpHeaders.Authorization, "Bearer never-issued")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun logout_closes_the_session_for_protected_routes() = testServerApplication {
        val session = signIn("logout-access@example.com")

        val loggedOut = client.post(LOGOUT_ROUTE) {
            header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
        }
        val afterLogout = client.get("/customer/${session.customerId}") {
            header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
        }

        assertEquals(HttpStatusCode.OK, loggedOut.status)
        assertEquals(HttpStatusCode.Unauthorized, afterLogout.status)
    }

    @Test
    fun logout_also_kills_the_refresh_token() = testServerApplication {
        // Dropping only the access token would leave a client able to mint a new one a minute
        // later — the sign-out would undo itself.
        val session = signIn("logout-refresh@example.com")

        client.post(LOGOUT_ROUTE) {
            header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
        }
        val response = client.post(REFRESH_ROUTE) {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(RefreshRequestDto(session.refreshToken)))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    private suspend fun HttpClient.signInResponse(email: String): AuthResponseDto {
        val request = AuthRequestDto(
            provider = AuthProvider.MANUAL,
            email = email,
            password = "secret123",
            firstName = "Session",
            lastName = "Test",
        )
        return post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }.decodeJson()
    }

    private suspend fun io.ktor.server.testing.ApplicationTestBuilder.signIn(
        email: String,
    ): Session {
        val response = client.signInResponse(email)
        return Session(
            customerId = response.customer.id,
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )
    }

    private data class Session(
        val customerId: String,
        val accessToken: String,
        val refreshToken: String,
    )

    private companion object {
        const val REFRESH_ROUTE = "/auth/refresh"
        const val LOGOUT_ROUTE = "/auth/logout"
    }
}