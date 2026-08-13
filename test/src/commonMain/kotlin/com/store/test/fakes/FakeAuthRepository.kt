package com.store.test.fakes

import com.feature.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.CompletableDeferred
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

/**
 * Shared test double for [AuthRepository].
 *
 * - [authorizeResult] is what [authorize] returns.
 * - [lastAuthorizeRequest] captures the last request for assertions.
 * - [gate] (optional): when set, [authorize] suspends on it until the test completes the deferred,
 *   so a test can observe the in-flight `isLoading = true` state before the result is delivered.
 */
class FakeAuthRepository : AuthRepository {
    var authorizeResult: ApiResult<AuthResponse, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)
    var lastAuthorizeRequest: AuthRequest? = null
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun authorize(request: AuthRequest): ApiResult<AuthResponse, NetworkError> {
        lastAuthorizeRequest = request
        gate?.await()
        return authorizeResult
    }

    override suspend fun signOut(): EmptyResult<NetworkError> = ApiResult.Success(Unit)
}
