package com.store.core.network.api

import com.store.core.domain.ApiResult
import com.store.core.network.dto.RefreshRequestDto
import com.store.core.network.dto.SessionTokensDto
import com.store.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.cmp.store.network.NetworkError

internal class SessionRefreshCall(private val client: HttpClient) {

    suspend fun refresh(refreshToken: String): ApiResult<SessionTokensDto, NetworkError> =
        safeApiCall {
            client.post(REFRESH_PATH) {
                attributes.put(AuthCircuitBreaker, Unit)
                contentType(ContentType.Application.Json)
                setBody(RefreshRequestDto(refreshToken))
            }
        }

    internal companion object {
        const val REFRESH_PATH = "auth/refresh"
    }
}