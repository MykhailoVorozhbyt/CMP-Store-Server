package com.feature.authentication.data

import com.feature.authentication.data.data_source.AuthDataSource
import com.feature.authentication.data.mappers.toAuthResponse
import com.feature.authentication.data.mappers.toDto
import com.feature.authentication.domain.repository.AuthRepository
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import com.store.core.domain.mapSuccess
import com.store.core.domain.onSuccess
import com.store.core.network.utils.isSessionTerminal
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.utils.extension.runCatchingCancellable
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import org.cmp.store.network.NetworkError

class DefaultAuthRepository(
    private val api: AuthDataSource,
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
) : AuthRepository {

    override suspend fun authorize(request: AuthRequest): ApiResult<AuthResponse, NetworkError> =
        api.authorize(request.toDto())
            .mapSuccess { it.toAuthResponse() }
            .onSuccess { storeSession(it) }

    override suspend fun signOut(): EmptyResult<NetworkError> {
        val serverResult = api.logout()
        if (serverResult is ApiResult.Error && !serverResult.error.isSessionTerminal) {
            return serverResult
        }
        return runCatchingCancellable {
            localAuthSessionDataSource.signOut()
            api.clearCachedTokens()
        }.fold(
            onSuccess = { ApiResult.Success(Unit) },
            onFailure = { ApiResult.Error(NetworkError.UNKNOWN) },
        )
    }

    private suspend fun storeSession(response: AuthResponse) {
        localAuthSessionDataSource.setSession(
            userId = response.customer.id,
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
    }
}
