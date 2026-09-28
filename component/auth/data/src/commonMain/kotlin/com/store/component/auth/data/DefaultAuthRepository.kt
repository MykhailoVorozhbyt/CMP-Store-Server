package com.store.component.auth.data

import com.store.component.auth.data.data_source.AuthDataSource
import com.store.component.auth.data.mappers.toAuthError
import com.store.component.auth.data.mappers.toAuthResponse
import com.store.component.auth.data.mappers.toDto
import com.store.component.auth.domain_api.AuthRepository
import com.store.component.auth.model.AuthError
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.core.domain.EmptyResult
import com.store.core.network.utils.isSessionTerminal
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.utils.extension.runCatchingCancellable
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse

class DefaultAuthRepository(
    private val api: AuthDataSource,
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
) : AuthRepository {

    override suspend fun authorize(request: AuthRequest): ApiResult<AuthResponse, AuthError> {
        return when (val result = api.authorize(request.toDto())) {
            is ApiResult.Error -> ApiResult.Error(result.error.toAuthError())
            is ApiResult.Success -> {
                val response = result.data.toAuthResponse()
                storeSession(response)
                ApiResult.Success(response)
            }
        }
    }

    override suspend fun signOut(): EmptyResult<AuthError> {
        val serverResult = api.logout()
        if (serverResult is ApiResult.Error && !serverResult.error.isSessionTerminal) {
            return ApiResult.Error(serverResult.error.toAuthError())
        }
        return runCatchingCancellable {
            localAuthSessionDataSource.signOut()
            api.clearCachedTokens()
        }.fold(
            onSuccess = { ApiResult.Success(Unit) },
            onFailure = { ApiResult.Error(AuthError.Common(DataError.Unknown)) },
        )
    }

    override suspend fun currentUserId(): String? = localAuthSessionDataSource.currentUserId()

    private suspend fun storeSession(response: AuthResponse) {
        localAuthSessionDataSource.setSession(
            userId = response.customer.id,
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
    }
}
