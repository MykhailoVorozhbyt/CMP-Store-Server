package com.feature.authentication.data.data_source

import com.feature.authentication.data.model.AuthRequestDto
import com.feature.authentication.data.model.AuthResponseDto
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import com.store.core.network.utils.clearBearerTokenCache
import com.store.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.cmp.store.network.NetworkError

interface AuthDataSource {
    suspend fun authorize(request: AuthRequestDto): ApiResult<AuthResponseDto, NetworkError>
    suspend fun logout(): EmptyResult<NetworkError>
    fun clearCachedTokens()
}

class KtorAuthDataSource(private val client: HttpClient) : AuthDataSource {

    override suspend fun authorize(request: AuthRequestDto): ApiResult<AuthResponseDto, NetworkError> =
        safeApiCall { client.post(AUTHORIZE) { setBody(request) } }

    override suspend fun logout(): EmptyResult<NetworkError> =
        safeApiCall { client.post(LOGOUT) }

    override fun clearCachedTokens() = client.clearBearerTokenCache()

    private companion object {
        private const val AUTHORIZE = "auth/authorize"
        private const val LOGOUT = "auth/logout"
    }
}
