package com.store.component.auth.usecase

import com.store.component.auth.domain_api.GoogleSignInService
import com.store.component.auth.model.GoogleSignInError
import com.store.component.auth.model.request.AuthUserRequest
import com.store.core.domain.ApiResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestGoogleAccountUseCaseTest {

    @Test
    fun returns_the_account_picked_by_the_google_sign_in_service() = runTest {
        val account = AuthUserRequest(uid = "google-uid", displayName = "Name", email = "user@example.com")
        val service = object : GoogleSignInService {
            override suspend fun signIn(): ApiResult<AuthUserRequest, GoogleSignInError> = ApiResult.Success(account)
        }

        assertEquals(ApiResult.Success(account), RequestGoogleAccountUseCase(service)())
    }
}
