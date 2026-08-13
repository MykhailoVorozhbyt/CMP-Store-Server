package org.cmp.store.features.auth.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val DELIMITER = ":"
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private val secureRandom = SecureRandom()
    private val secretKeyFactory = ThreadLocal.withInitial {
        SecretKeyFactory.getInstance(ALGORITHM)
    }

    suspend fun hash(password: String): String {
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        val hash = deriveHash(password = password, salt = salt, iterations = ITERATIONS)
        return listOf(
            ITERATIONS.toString(),
            Base64.getEncoder().encodeToString(salt),
            Base64.getEncoder().encodeToString(hash)
        ).joinToString(DELIMITER)
    }

    suspend fun verify(
        password: String,
        storedHash: String,
    ): Boolean {
        val parts = storedHash.split(DELIMITER)
        if (parts.size != 3) {
            return false
        }

        val storedIterations = parts[0].toIntOrNull() ?: return false
        val salt = runCatching { Base64.getDecoder().decode(parts[1]) }.getOrNull() ?: return false
        val expectedHash =
            runCatching { Base64.getDecoder().decode(parts[2]) }.getOrNull() ?: return false
        val actualHash = deriveHash(
            password = password,
            salt = salt,
            iterations = storedIterations
        )
        return MessageDigest.isEqual(expectedHash, actualHash)
    }

    private suspend fun deriveHash(
        password: String,
        salt: ByteArray,
        iterations: Int,
    ): ByteArray = withContext(Dispatchers.Default) {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH)
        secretKeyFactory.get().generateSecret(spec).encoded
    }

}
