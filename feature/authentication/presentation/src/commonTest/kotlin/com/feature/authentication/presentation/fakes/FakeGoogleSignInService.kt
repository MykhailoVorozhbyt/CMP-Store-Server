package com.feature.authentication.presentation.fakes

import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.domain.model.request.AuthUserRequest
import com.feature.authentication.domain.repository.GoogleSignInService
import kotlinx.coroutines.CompletableDeferred
import com.store.core.domain.ApiResult

/**
 * Test double for [GoogleSignInService]. Returns a configurable [signInResult]; when [gate] is set,
 * [signIn] suspends on it so the test can observe the in-flight `google.isLoading = true` state.
 */
class FakeGoogleSignInService : GoogleSignInService {
    var signInResult: ApiResult<AuthUserRequest, GoogleSignInError> =
        ApiResult.Error(GoogleSignInError.InvalidState())
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun signIn(): ApiResult<AuthUserRequest, GoogleSignInError> {
        gate?.await()
        return signInResult
    }
}