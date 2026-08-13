package com.store.core.network.dto

import kotlinx.serialization.Serializable

/**
 * Wire shapes for `POST /auth/refresh`. They live here rather than in a feature module
 * because the refresh call is made by the HTTP client itself, below any feature: by the
 * time a repository sees a response, rotation has already happened invisibly.
 */
@Serializable
internal data class RefreshRequestDto(
    val refreshToken: String,
)

@Serializable
internal data class SessionTokensDto(
    val accessToken: String,
    val refreshToken: String,
)
