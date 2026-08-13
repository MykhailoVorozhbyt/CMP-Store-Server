package org.cmp.store.features.session.service

import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SessionTokenFactoryTest {

    @Test
    fun generated_token_carries_256_bits_of_entropy() {
        val decoded = Base64.getUrlDecoder().decode(SessionTokenFactory.generate())

        // The whole reason this class exists instead of UUID.randomUUID(), which yields 122.
        assertEquals(32, decoded.size)
    }

    @Test
    fun generated_tokens_are_url_safe_and_unpadded() {
        // These strings travel in Authorization headers and JSON bodies. Standard Base64
        // would emit '+', '/' and '=', which survive neither reliably.
        repeat(TOKEN_SAMPLE) {
            val token = SessionTokenFactory.generate()
            assertTrue(
                token.none { it == '+' || it == '/' || it == '=' },
                "Token is not url-safe: $token"
            )
        }
    }

    @Test
    fun generated_tokens_do_not_repeat() {
        val tokens = List(TOKEN_SAMPLE) { SessionTokenFactory.generate() }

        // A collision here would mean two customers sharing one session.
        assertEquals(tokens.size, tokens.toSet().size)
    }

    @Test
    fun hash_is_deterministic() {
        val token = SessionTokenFactory.generate()

        // Lookup by hash is the only way a refresh token is ever found, so an unstable
        // hash would silently make every refresh fail with "unknown token".
        assertEquals(SessionTokenFactory.hash(token), SessionTokenFactory.hash(token))
    }

    @Test
    fun hash_differs_between_tokens() {
        assertNotEquals(
            SessionTokenFactory.hash(SessionTokenFactory.generate()),
            SessionTokenFactory.hash(SessionTokenFactory.generate())
        )
    }

    @Test
    fun hash_does_not_leak_the_token() {
        val token = SessionTokenFactory.generate()

        // The point of hashing: a leaked database dump must not contain usable credentials.
        val hash = SessionTokenFactory.hash(token)
        assertNotEquals(token, hash)
        assertTrue(!hash.contains(token), "Hash embeds the raw token")
    }

    private companion object {
        const val TOKEN_SAMPLE = 500
    }
}
