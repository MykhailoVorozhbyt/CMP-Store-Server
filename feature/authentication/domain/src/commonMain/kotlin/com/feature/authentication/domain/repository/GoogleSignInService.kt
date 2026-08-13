package com.feature.authentication.domain.repository

import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.domain.model.request.AuthUserRequest
import com.store.core.domain.ApiResult

interface GoogleSignInService {
    suspend fun signIn(): ApiResult<AuthUserRequest, GoogleSignInError>
}
