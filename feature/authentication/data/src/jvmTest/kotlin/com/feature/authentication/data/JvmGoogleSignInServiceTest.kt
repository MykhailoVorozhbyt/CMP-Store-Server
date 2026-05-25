package com.feature.authentication.data

import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.domain.model.request.AuthUserRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import com.store.core.domain.ApiResult
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Base64
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class JvmGoogleSignInServiceTest {

    private val callbackDeliveryClient = HttpClient(OkHttp)

    @AfterTest
    fun tearDown() {
        callbackDeliveryClient.close()
    }

    private fun extractQueryParam(url: String, key: String): String =
        URI(url).rawQuery.orEmpty()
            .split("&")
            .map { it.split("=", limit = 2) }
            .first { it[0] == key }
            .let { URLDecoder.decode(it[1], StandardCharsets.UTF_8) }

    private fun fakeJwt(payloadJson: String): String {
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val header =
            encoder.encodeToString("{\"alg\":\"none\"}".toByteArray(StandardCharsets.UTF_8))
        val payload = encoder.encodeToString(payloadJson.toByteArray(StandardCharsets.UTF_8))
        return "$header.$payload.fake-signature"
    }

    /** Simulates Google redirecting the user's browser back to our local server. */
    private fun deliverCallback(redirectUrl: String, query: String) = runBlocking {
        callbackDeliveryClient.get("$redirectUrl?$query")
    }

    private fun successfulCallbackLauncher(): (String) -> Unit = { authUrl ->
        val state = extractQueryParam(authUrl, "state")
        val redirectUrl = extractQueryParam(authUrl, "redirect_uri")
        deliverCallback(redirectUrl, "state=$state&code=fake-auth-code")
    }

    // ---- success ----
    @Test
    fun signIn_with_completed_authorization_returns_authenticated_user() = runTest {
        var capturedNonce = ""

        val mockEngine = MockEngine { _ ->
            val idToken = fakeJwt(
                """{"sub":"user-42","email":"person@example.com","name":"Jane Doe","nonce":"$capturedNonce"}"""
            )
            respond(
                content = """{"id_token":"$idToken","access_token":"fake-access-token"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val service = JvmGoogleSignInService(
            httpClient = HttpClient(mockEngine),
            clientSecret = "test-secret",
            browserLauncher = { authUrl ->
                capturedNonce = extractQueryParam(authUrl, "nonce")
                val state = extractQueryParam(authUrl, "state")
                val redirectUrl = extractQueryParam(authUrl, "redirect_uri")
                deliverCallback(redirectUrl, "state=$state&code=fake-auth-code")
            },
        )

        val result = service.signIn()

        assertIs<ApiResult.Success<AuthUserRequest>>(result)
        val user = result.data
        assertEquals("user-42", user.uid)
        assertEquals("person@example.com", user.email)
        assertEquals("Jane Doe", user.displayName)
    }

    // ---- AuthorizationDenied (end-to-end through the real local server) ----

    @Test
    fun signIn_when_user_denies_access_returns_failure() = runTest {
        val mockEngine =
            MockEngine { error("token endpoint should not be called when authorization is denied") }

        val service = JvmGoogleSignInService(
            httpClient = HttpClient(mockEngine),
            clientSecret = "test-secret",
            browserLauncher = { authUrl ->
                val state = extractQueryParam(authUrl, "state")
                val redirectUrl = extractQueryParam(authUrl, "redirect_uri")
                deliverCallback(
                    redirectUrl,
                    "state=$state&error=access_denied&error_description=User+denied+access"
                )
            },
        )

        val result = service.signIn()

        assertIs<ApiResult.Error<GoogleSignInError>>(result)
        assertIs<GoogleSignInError.AuthorizationDenied>(result.error)
    }

    // ---- TokenExchangeFailed ----

    @Test
    fun signIn_when_token_endpoint_returns_an_error_status_returns_TokenExchangeFailed_failure() =
        runTest {
            val mockEngine = MockEngine { _ ->
                respond(
                    content = """{"error":"invalid_grant"}""",
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

            val service = JvmGoogleSignInService(
                httpClient = HttpClient(mockEngine),
                clientSecret = "test-secret",
                browserLauncher = successfulCallbackLauncher(),
            )

            val result = service.signIn()

            assertIs<ApiResult.Error<GoogleSignInError>>(result)
            val error = result.error
            assertIs<GoogleSignInError.TokenExchangeFailed>(error)
            assertEquals(400, error.statusCode)
            assertEquals("""{"error":"invalid_grant"}""", error.body)
        }

    // ---- MissingIdToken ----

    @Test
    fun signIn_when_token_response_has_no_id_token_returns_MissingIdToken_failure() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"access_token":"fake-access-token"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val service = JvmGoogleSignInService(
            httpClient = HttpClient(mockEngine),
            clientSecret = "test-secret",
            browserLauncher = successfulCallbackLauncher(),
        )

        val result = service.signIn()

        assertIs<ApiResult.Error<GoogleSignInError>>(result)
        assertIs<GoogleSignInError.MissingIdToken>(result.error)
    }

    // ---- NonceMismatch ----

    @Test
    fun signIn_when_id_token_nonce_does_not_match_the_request_nonce_returns_NonceMismatch_failure() =
        runTest {
            val mockEngine = MockEngine { _ ->
                val idToken = fakeJwt(
                    """{"sub":"user-42","email":"person@example.com","name":"Jane Doe","nonce":"unexpected-nonce"}"""
                )
                respond(
                    content = """{"id_token":"$idToken","access_token":"fake-access-token"}""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }

            val service = JvmGoogleSignInService(
                httpClient = HttpClient(mockEngine),
                clientSecret = "test-secret",
                browserLauncher = successfulCallbackLauncher(),
            )

            val result = service.signIn()

            assertIs<ApiResult.Error<GoogleSignInError>>(result)
            assertIs<GoogleSignInError.NonceMismatch>(result.error)
        }

    // ---- DesktopNotSupported ----

    @Test
    fun signIn_when_browser_launch_reports_unsupported_desktop_propagates_DesktopNotSupported_failure() =
        runTest {
            val mockEngine =
                MockEngine { error("token endpoint should not be called when the browser cannot be launched") }

            val service = JvmGoogleSignInService(
                httpClient = HttpClient(mockEngine),
                clientSecret = "test-secret",
                browserLauncher = { throw GoogleSignInError.DesktopNotSupported() },
            )

            val result = service.signIn()

            assertIs<ApiResult.Error<GoogleSignInError>>(result)
            assertIs<GoogleSignInError.DesktopNotSupported>(result.error)
        }

    // ---- Timeout ----

    @Test
    fun signIn_when_no_callback_ever_arrives_returns_Timeout_failure() = runTest {
        val mockEngine =
            MockEngine { error("token endpoint should not be called when sign-in times out") }

        val service = JvmGoogleSignInService(
            httpClient = HttpClient(mockEngine),
            clientSecret = "test-secret",
            // simulates the user never finishing in the browser — runTest's virtual
            // clock fast-forwards past AUTH_TIMEOUT_SECONDS, so this doesn't actually wait
            browserLauncher = { },
        )

        val result = service.signIn()

        assertIs<ApiResult.Error<GoogleSignInError>>(result)
        assertIs<GoogleSignInError.Timeout>(result.error)
    }
}