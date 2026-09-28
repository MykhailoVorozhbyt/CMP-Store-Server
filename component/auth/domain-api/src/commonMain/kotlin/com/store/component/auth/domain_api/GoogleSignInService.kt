package com.store.component.auth.domain_api

import com.store.component.auth.model.GoogleSignInError
import com.store.component.auth.model.request.AuthUserRequest
import com.store.core.domain.ApiResult

interface GoogleSignInService {
    suspend fun signIn(): ApiResult<AuthUserRequest, GoogleSignInError>
}
