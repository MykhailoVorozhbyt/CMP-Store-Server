package com.feature.authentication.domain.repository

import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

interface AuthRepository {
    suspend fun authorize(request: AuthRequest): ApiResult<AuthResponse, NetworkError>
    suspend fun signOut(): EmptyResult<NetworkError>
}
