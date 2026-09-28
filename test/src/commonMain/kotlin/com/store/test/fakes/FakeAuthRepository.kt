package com.store.test.fakes

import com.store.component.auth.domain_api.AuthRepository
import com.store.component.auth.model.AuthError
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.core.domain.EmptyResult
import kotlinx.coroutines.CompletableDeferred
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse

/**
 * Shared test double for [AuthRepository].
 *
 * - [authorizeResult] is what [authorize] returns.
 * - [lastAuthorizeRequest] captures the last request for assertions.
 * - [gate] (optional): when set, [authorize] suspends on it until the test completes the deferred,
 *   so a test can observe the in-flight `isLoading = true` state before the result is delivered.
 */
class FakeAuthRepository : AuthRepository {
    var authorizeResult: ApiResult<AuthResponse, AuthError> =
        ApiResult.Error(AuthError.Common(DataError.Unknown))
    var lastAuthorizeRequest: AuthRequest? = null
    var gate: CompletableDeferred<Unit>? = null
    var currentUserId: String? = null

    override suspend fun authorize(request: AuthRequest): ApiResult<AuthResponse, AuthError> {
        lastAuthorizeRequest = request
        gate?.await()
        return authorizeResult
    }

    override suspend fun signOut(): EmptyResult<AuthError> = ApiResult.Success(Unit)

    override suspend fun currentUserId(): String? = currentUserId
}
