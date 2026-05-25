package com.feature.authentication.data.fakes

import com.feature.authentication.data.data_source.AuthDataSource
import com.feature.authentication.data.model.AuthRequestDto
import com.feature.authentication.data.model.AuthResponseDto
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

class FakeAuthDataSource : AuthDataSource {
    var authorizeResult: ApiResult<AuthResponseDto, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)
    var lastAuthorizeRequest: AuthRequestDto? = null
    var logoutResult: EmptyResult<NetworkError> = ApiResult.Success(Unit)
    var logoutCallCount = 0
    var clearCachedTokensCallCount = 0

    override suspend fun authorize(request: AuthRequestDto): ApiResult<AuthResponseDto, NetworkError> {
        lastAuthorizeRequest = request
        return authorizeResult
    }

    override suspend fun logout(): EmptyResult<NetworkError> {
        logoutCallCount++
        return logoutResult
    }

    override fun clearCachedTokens() {
        clearCachedTokensCallCount++
    }
}
