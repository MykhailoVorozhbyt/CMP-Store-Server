package com.feature.authentication.data.oauth

import com.feature.authentication.domain.model.GoogleSignInError
import java.nio.charset.StandardCharsets
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JwtParserTest {

    private fun fakeJwt(payloadJson: String): String {
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val header = encoder.encodeToString("{\"alg\":\"none\"}".toByteArray(StandardCharsets.UTF_8))
        val payload = encoder.encodeToString(payloadJson.toByteArray(StandardCharsets.UTF_8))
        return "$header.$payload.fake-signature"
    }

    @Test
    fun decodeClaims_with_valid_payload_returns_parsed_claims() {
        val token = fakeJwt(
            """{"sub":"user-1","email":"jane@example.com","name":"Jane Doe","nonce":"nonce-123"}"""
        )

        val claims = JwtParser.decodeClaims(token)

        assertEquals("user-1", claims.sub)
        assertEquals("jane@example.com", claims.email)
        assertEquals("Jane Doe", claims.name)
        assertEquals("nonce-123", claims.nonce)
    }

    @Test
    fun decodeClaims_with_required_field_missing_throws_InvalidIdToken() {
        val token = fakeJwt("""{"email":"missing-sub@example.com"}""") // no "sub"

        assertFailsWith<GoogleSignInError.InvalidIdToken> {
            JwtParser.decodeClaims(token)
        }
    }

    @Test
    fun decodeClaims_with_no_dots_throws_InvalidIdToken() {
        assertFailsWith<GoogleSignInError.InvalidIdToken> {
            JwtParser.decodeClaims("not-a-jwt")
        }
    }

    @Test
    fun decodeClaims_with_invalid_base64_payload_throws_InvalidIdToken() {
        val token = "header.***not-base64***.signature"

        assertFailsWith<GoogleSignInError.InvalidIdToken> {
            JwtParser.decodeClaims(token)
        }
    }

    @Test
    fun decodeClaims_with_non_json_payload_throws_InvalidIdToken() {
        val token = fakeJwt("this is not json")

        assertFailsWith<GoogleSignInError.InvalidIdToken> {
            JwtParser.decodeClaims(token)
        }
    }
}
