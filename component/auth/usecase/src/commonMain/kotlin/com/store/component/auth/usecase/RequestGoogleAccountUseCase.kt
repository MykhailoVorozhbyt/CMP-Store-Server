package com.store.component.auth.usecase

import com.store.component.auth.domain_api.GoogleSignInService
import com.store.component.auth.model.GoogleSignInError
import com.store.component.auth.model.request.AuthUserRequest
import com.store.core.domain.ApiResult

class RequestGoogleAccountUseCase(
    private val googleSignInService: GoogleSignInService,
) {
    suspend operator fun invoke(): ApiResult<AuthUserRequest, GoogleSignInError> = googleSignInService.signIn()
}
