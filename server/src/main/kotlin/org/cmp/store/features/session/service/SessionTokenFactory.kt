package org.cmp.store.features.session.service

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object SessionTokenFactory {

    private val secureRandom = SecureRandom()

    fun generate(): String {
        val bytes = ByteArray(TOKEN_BYTES)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun hash(token: String): String = Base64.getEncoder().encodeToString(
        MessageDigest.getInstance(HASH_ALGORITHM).digest(token.toByteArray(Charsets.UTF_8))
    )

    private const val TOKEN_BYTES = 32
    private const val HASH_ALGORITHM = "SHA-256"
}
