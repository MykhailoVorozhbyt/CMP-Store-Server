package org.cmp.store.features.auth.service

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHasherTest {

    @Test
    fun hash_output_verifies_with_same_password() = runBlocking {
        val password = "secret123"

        val hash = PasswordHasher.hash(password)

        assertTrue(PasswordHasher.verify(password, hash))
    }

    @Test
    fun verify_fails_with_wrong_password() = runBlocking {
        val hash = PasswordHasher.hash("secret123")

        val isValid = PasswordHasher.verify("wrong-password", hash)

        assertFalse(isValid)
    }

    @Test
    fun verify_fails_for_malformed_hash() = runBlocking {
        val malformedHashes = listOf(
            "",
            "invalid",
            "120000:not-base64:still-not-base64",
            "abc:def:ghi",
            "120000:Zm9v",
        )

        malformedHashes.forEach { malformed ->
            assertFalse(PasswordHasher.verify("secret123", malformed))
        }
    }

    @Test
    fun hashing_same_password_twice_gives_different_hashes() = runBlocking {
        val password = "secret123"

        val first = PasswordHasher.hash(password)
        val second = PasswordHasher.hash(password)

        assertNotEquals(first, second)
        assertTrue(PasswordHasher.verify(password, first))
        assertTrue(PasswordHasher.verify(password, second))
    }
}
