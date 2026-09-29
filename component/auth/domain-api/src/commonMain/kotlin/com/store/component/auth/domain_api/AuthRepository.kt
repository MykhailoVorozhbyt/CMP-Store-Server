package com.store.component.auth.domain_api

import com.store.component.auth.model.AuthError
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse

interface AuthRepository {
    suspend fun authorize(request: AuthRequest): ApiResult<AuthResponse, AuthError>
    suspend fun signOut(): EmptyResult<AuthError>
    suspend fun currentUserId(): String?
}
