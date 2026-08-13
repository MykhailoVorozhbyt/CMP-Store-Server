package org.cmp.store.features

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.auth.dto.AuthRequestDto
import org.cmp.store.features.auth.dto.AuthResponseDto
import org.cmp.store.features.auth.dto.RefreshRequestDto
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.RateLimitGroup
import org.cmp.store.utils.decodeJson
import org.cmp.store.utils.testJson
import org.cmp.store.utils.testServerApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AuthRoutesIntegrationTest {

    @Test
    fun manual_sign_in_creates_new_account() = testServerApplication {
        val request = AuthRequestDto(
            provider = AuthProvider.MANUAL,
            email = "manual-new@example.com",
            password = "secret123",
            firstName = "Manual",
            lastName = "New",
        )

        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }

        val body = response.decodeJson<AuthResponseDto>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(body.isNewAccount)
        assertEquals(AuthProvider.MANUAL, body.provider)
        assertEquals("manual-new@example.com", body.customer.email)
        assertEquals("Manual", body.customer.firstName)
        assertEquals("New", body.customer.lastName)
        assertTrue(body.accessToken.isNotBlank())
    }

    @Test
    fun manual_sign_in_returns_existing_account() = testServerApplication {
        val request = AuthRequestDto(
            provider = AuthProvider.MANUAL,
            email = "manual-existing@example.com",
            password = "secret123",
            firstName = "Manual",
            lastName = "Existing",
        )

        val created = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }.decodeJson<AuthResponseDto>()

        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }

        val body = response.decodeJson<AuthResponseDto>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertFalse(body.isNewAccount)
        assertEquals(created.customer.id, body.customer.id)
        assertEquals(AuthProvider.MANUAL, body.provider)
        assertNotEquals(created.accessToken, body.accessToken)
    }

    @Test
    fun manual_sign_in_fails_with_wrong_password() = testServerApplication {
        val request = AuthRequestDto(
            provider = AuthProvider.MANUAL,
            email = "manual-wrong-password@example.com",
            password = "secret123",
        )
        client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }

        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(
                testJson.encodeToString(
                    request.copy(password = "wrong-password")
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(NetworkError.INVALID_CREDENTIALS.name, response.bodyAsText())
    }

    @Test
    fun blank_email_returns_400() = testServerApplication {
        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(
                testJson.encodeToString(
                    AuthRequestDto(
                        provider = AuthProvider.MANUAL,
                        email = "   ",
                        password = "secret123",
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(NetworkError.EMAIL_REQUIRED.name, response.bodyAsText())
    }

    @Test
    fun blank_password_for_manual_auth_returns_400() = testServerApplication {
        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(
                testJson.encodeToString(
                    AuthRequestDto(
                        provider = AuthProvider.MANUAL,
                        email = "manual-blank-password@example.com",
                        password = " ",
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(NetworkError.PASSWORD_REQUIRED.name, response.bodyAsText())
    }

    @Test
    fun missing_providerUserId_for_social_auth_returns_400() = testServerApplication {
        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(
                testJson.encodeToString(
                    AuthRequestDto(
                        provider = AuthProvider.GOOGLE,
                        email = "social-missing-id@example.com",
                        displayName = "Social Missing",
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(NetworkError.PROVIDER_USER_ID_REQUIRED.name, response.bodyAsText())
    }

    @Test
    fun malformed_JSON_returns_400() = testServerApplication {
        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody("""{"provider":"MANUAL","email":"broken@example.com","password":""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(NetworkError.SERIALIZATION.name, response.bodyAsText())
    }

    @Test
    fun existing_social_credential_returns_existing_user() = testServerApplication {
        val request = AuthRequestDto(
            provider = AuthProvider.GOOGLE,
            email = "social-existing@example.com",
            providerUserId = "google-uid-1",
            displayName = "Social Existing",
        )

        val created = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }.decodeJson<AuthResponseDto>()

        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }

        val body = response.decodeJson<AuthResponseDto>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertFalse(body.isNewAccount)
        assertEquals(created.customer.id, body.customer.id)
        assertEquals(AuthProvider.GOOGLE, body.provider)
    }

    @Test
    fun existing_customer_by_email_plus_new_social_credential_links_correctly() =
        testServerApplication {
            val manual = AuthRequestDto(
                provider = AuthProvider.MANUAL,
                email = "linked-social@example.com",
                password = "secret123",
                firstName = "Linked",
                lastName = "Manual",
            )
            val manualResponse = client.post("/auth/authorize") {
                contentType(ContentType.Application.Json)
                setBody(testJson.encodeToString(manual))
            }.decodeJson<AuthResponseDto>()

            val social = AuthRequestDto(
                provider = AuthProvider.GOOGLE,
                email = "linked-social@example.com",
                providerUserId = "google-linked-uid",
                displayName = "Linked Social",
            )
            val response = client.post("/auth/authorize") {
                contentType(ContentType.Application.Json)
                setBody(testJson.encodeToString(social))
            }

            val body = response.decodeJson<AuthResponseDto>()
            assertEquals(HttpStatusCode.OK, response.status)
            assertFalse(body.isNewAccount)
            assertEquals(manualResponse.customer.id, body.customer.id)
            assertEquals(AuthProvider.GOOGLE, body.provider)
        }

    @Test
    fun manual_sign_in_on_google_account_email_returns_409() = testServerApplication {
        val google = AuthRequestDto(
            provider = AuthProvider.GOOGLE,
            email = "google-owned@example.com",
            providerUserId = "google-owned-uid",
            displayName = "Google Owned",
        )
        client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(google))
        }

        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(
                testJson.encodeToString(
                    AuthRequestDto(
                        provider = AuthProvider.MANUAL,
                        email = "google-owned@example.com",
                        password = "attacker-password",
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals(NetworkError.ACCOUNT_HAS_NO_PASSWORD.name, response.bodyAsText())
    }

    @Test
    fun mismatched_social_credential_returns_401() = testServerApplication {
        val original = AuthRequestDto(
            provider = AuthProvider.GOOGLE,
            email = "social-mismatch@example.com",
            providerUserId = "google-uid-1",
            displayName = "Social One",
        )
        client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(original))
        }

        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(
                testJson.encodeToString(
                    original.copy(providerUserId = "google-uid-2")
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(NetworkError.INVALID_CREDENTIALS.name, response.bodyAsText())
    }

    @Test
    fun facebook_sign_in_creates_new_account() = testServerApplication {
        val request = AuthRequestDto(
            provider = AuthProvider.FACEBOOK,
            email = "facebook-new@example.com",
            providerUserId = "facebook-uid-1",
            displayName = "Facebook New",
        )

        val response = client.post("/auth/authorize") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(request))
        }

        val body = response.decodeJson<AuthResponseDto>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(body.isNewAccount)
        assertEquals(AuthProvider.FACEBOOK, body.provider)
        assertEquals("facebook-new@example.com", body.customer.email)
        assertTrue(body.accessToken.isNotBlank())
    }

    @Test
    fun repeated_password_guesses_for_one_email_are_throttled() = testServerApplication {
        val email = "throttled@example.com"
        signUp(email = email, password = "secret123")

        // The sign-up above already spent one slot of the (address, email) bucket.
        val statuses = (1..AUTH_RATE_LIMIT_PER_MINUTE).map {
            attemptSignIn(email = email, password = "wrong-password").status
        }

        assertTrue(
            statuses.dropLast(1).all { it == HttpStatusCode.Unauthorized },
            "guesses before the limit should be rejected on credentials, got $statuses"
        )
        assertEquals(HttpStatusCode.TooManyRequests, statuses.last())
    }

    @Test
    fun throttling_one_email_leaves_another_email_unaffected() = testServerApplication {
        val attacked = "attacked@example.com"
        signUp(email = attacked, password = "secret123")
        repeat(AUTH_RATE_LIMIT_PER_MINUTE) {
            attemptSignIn(email = attacked, password = "wrong-password")
        }

        // Same client address, different email — this is what keying on the pair buys us:
        // one hammered account must not lock out everyone behind a shared NAT.
        val bystander = attemptSignIn(email = "bystander@example.com", password = "secret123")

        assertNotEquals(HttpStatusCode.TooManyRequests, bystander.status)
    }

    @Test
    fun throttled_response_carries_the_shared_error_code() = testServerApplication {
        val email = "throttled-body@example.com"
        repeat(AUTH_RATE_LIMIT_PER_MINUTE + 1) {
            attemptSignIn(email = email, password = "secret123")
        }

        val response = attemptSignIn(email = email, password = "secret123")

        assertEquals(HttpStatusCode.TooManyRequests, response.status)
        assertEquals(NetworkError.TOO_MANY_REQUESTS.name, response.bodyAsText())
    }

    @Test
    fun exhausting_the_authorize_budget_leaves_refresh_working() = testServerApplication {
        val refreshToken = signUp(email = "own-bucket@example.com", password = "secret123")
            .decodeJson<AuthResponseDto>()
            .refreshToken

        // Malformed bodies still count against the authorize bucket — that is the point of
        // giving refresh its own group: a stranger behind the same NAT cannot spend it for us.
        repeat(AUTH_RATE_LIMIT_PER_MINUTE + 1) {
            client.post("/auth/authorize") {
                contentType(ContentType.Application.Json)
                setBody("{ not json")
            }
        }

        val response = client.post("/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(RefreshRequestDto(refreshToken)))
        }

        assertNotEquals(HttpStatusCode.TooManyRequests, response.status)
    }

    private suspend fun ApplicationTestBuilder.signUp(
        email: String,
        password: String,
    ) = attemptSignIn(email = email, password = password)

    private suspend fun ApplicationTestBuilder.attemptSignIn(
        email: String,
        password: String,
    ) = client.post("/auth/authorize") {
        contentType(ContentType.Application.Json)
        setBody(
            testJson.encodeToString(
                AuthRequestDto(
                    provider = AuthProvider.MANUAL,
                    email = email,
                    password = password,
                )
            )
        )
    }

    private companion object {
        /** Read from the source of truth so the tests cannot drift from the configured budget. */
        val AUTH_RATE_LIMIT_PER_MINUTE = RateLimitGroup.AUTH_AUTHORIZE.limit
    }
}
