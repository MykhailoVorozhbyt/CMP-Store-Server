package com.feature.authentication.data.oauth

import com.feature.authentication.domain.model.GoogleSignInError
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.util.Base64

object JwtParser {

    private const val JWT_PARTS = 3

    private val json = Json {
        ignoreUnknownKeys = true
    }

    internal fun decodeClaims(idToken: String): GoogleIdTokenClaims {
        val parts = idToken.split(".")
        if (parts.size != JWT_PARTS) {
            throw GoogleSignInError.InvalidIdToken()
        }
        return runCatching {
            val decoded = String(
                Base64.getUrlDecoder().decode(parts[1]),
                StandardCharsets.UTF_8
            )
            json.decodeFromString<GoogleIdTokenClaims>(decoded)
        }.getOrElse { throw GoogleSignInError.InvalidIdToken() }
    }

    @Serializable
    internal data class GoogleIdTokenClaims(
        val sub: String,
        val email: String,
        val name: String? = null,
        val nonce: String? = null,
    )

}