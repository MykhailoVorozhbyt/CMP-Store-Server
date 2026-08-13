package com.feature.authentication.data

import com.feature.authentication.data.fakes.FakeAuthDataSource
import com.store.test.fakes.FakeLocalAuthSessionDataSource
import com.feature.authentication.data.mappers.toDto
import com.feature.authentication.data.model.AuthResponseDto
import com.feature.authentication.data.models.testCustomer
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultAuthRepositoryTest {
    private val fakeApi = FakeAuthDataSource()
    private val fakeSession = FakeLocalAuthSessionDataSource()
    private val repository = DefaultAuthRepository(fakeApi, fakeSession)

    @Test
    fun authorize_returnsSuccessAndStoresSession_forNewManualUser() = runTest {
        authorizeStoresSessionOnSuccess(
            provider = AuthProvider.MANUAL,
            isNewAccount = true,
            accessToken = "manual-new-token",
            customerId = "manual-new-customer",
        )
    }

    @Test
    fun authorize_returnsErrorAndDoesNotStoreSession_forNewManualUser() = runTest {
        authorizeDoesNotStoreSessionOnError(
            request = manualRequest(password = "secret123"),
            error = NetworkError.SERVER_ERROR,
        )
    }

    @Test
    fun authorize_returnsSuccessAndStoresSession_forExistingManualUser() = runTest {
        authorizeStoresSessionOnSuccess(
            provider = AuthProvider.MANUAL,
            isNewAccount = false,
            accessToken = "manual-existing-token",
            customerId = "manual-existing-customer",
        )
    }

    @Test
    fun authorize_returnsErrorAndDoesNotStoreSession_forExistingManualUser() = runTest {
        authorizeDoesNotStoreSessionOnError(
            request = manualRequest(password = "wrong-password"),
            error = NetworkError.INVALID_CREDENTIALS,
        )
    }

    @Test
    fun authorize_returnsSuccessAndStoresSession_forNewGoogleUser() = runTest {
        authorizeStoresSessionOnSuccess(
            provider = AuthProvider.GOOGLE,
            isNewAccount = true,
            accessToken = "google-new-token",
            customerId = "google-new-customer",
        )
    }

    @Test
    fun authorize_returnsErrorAndDoesNotStoreSession_forNewGoogleUser() = runTest {
        authorizeDoesNotStoreSessionOnError(
            request = googleRequest(),
            error = NetworkError.SERVER_ERROR,
        )
    }

    @Test
    fun authorize_returnsSuccessAndStoresSession_forExistingGoogleUser() = runTest {
        authorizeStoresSessionOnSuccess(
            provider = AuthProvider.GOOGLE,
            isNewAccount = false,
            accessToken = "google-existing-token",
            customerId = "google-existing-customer",
        )
    }

    @Test
    fun authorize_returnsErrorAndDoesNotStoreSession_forExistingGoogleUser() = runTest {
        authorizeDoesNotStoreSessionOnError(
            request = googleRequest(),
            error = NetworkError.INVALID_CREDENTIALS,
        )
    }

    @Test
    fun signOut_revokesOnServerThenClearsSessionAndReturnsSuccess() = runTest {
        fakeSession.userId = "uid"
        fakeSession.accessToken = "token"
        fakeSession.refreshToken = "refresh"
        fakeApi.logoutResult = ApiResult.Success(Unit)

        val result = repository.signOut()

        assertEquals(1, fakeApi.logoutCallCount)
        assertTrue(fakeSession.signOutCalled)
        assertEquals(ApiResult.Success(Unit), result)
        assertNull(fakeSession.userId)
        assertNull(fakeSession.accessToken)
        assertNull(fakeSession.refreshToken)
    }

    @Test
    fun signOut_keepsTheSessionWhenTheServerRevokeFails() = runTest {
        // Sign-out is the server's call. Wiping locally on a failed revoke would leave a live
        // session on the other end that this device can no longer reach to close.
        fakeSession.userId = "uid"
        fakeSession.accessToken = "token"
        fakeSession.refreshToken = "refresh"
        fakeApi.logoutResult = ApiResult.Error(NetworkError.SERVER_ERROR)

        val result = repository.signOut()

        assertEquals(ApiResult.Error(NetworkError.SERVER_ERROR), result)
        assertFalse(fakeSession.signOutCalled)
        assertEquals("token", fakeSession.accessToken)
        assertEquals(0, fakeApi.clearCachedTokensCallCount)
    }

    @Test
    fun signOut_clearsTheSessionWhenTheServerSaysItIsAlreadyGone() = runTest {
        // A 401 on logout is the server confirming the session is dead, not a failure to
        // reach it. Reporting an error would leave the user unable to leave a session that
        // no longer exists anywhere.
        fakeSession.userId = "uid"
        fakeSession.accessToken = "token"
        fakeSession.refreshToken = "refresh"
        fakeApi.logoutResult = ApiResult.Error(NetworkError.UNAUTHORIZED)

        val result = repository.signOut()

        assertEquals(ApiResult.Success(Unit), result)
        assertTrue(fakeSession.signOutCalled)
        assertNull(fakeSession.accessToken)
    }

    @Test
    fun signOut_clearsTheTransportTokenCacheAfterStorage() = runTest {
        fakeSession.userId = "uid"
        fakeSession.accessToken = "token"
        fakeSession.refreshToken = "refresh"

        repository.signOut()

        // Without this the HTTP client keeps attaching the revoked token, so the first call
        // after the *next* sign-in 401s before it self-heals.
        assertEquals(1, fakeApi.clearCachedTokensCallCount)
    }

    @Test
    fun signOut_returnsFailureWhenSessionSourceThrows() = runTest {
        // A storage that cannot be cleared outranks a successful server revoke: the credentials
        // are still on the device, so this must not be reported as a clean sign-out.
        fakeApi.logoutResult = ApiResult.Success(Unit)
        fakeSession.signOutThrowable = IllegalStateException("boom")

        val result = repository.signOut()

        assertEquals(ApiResult.Error(NetworkError.UNKNOWN), result)
    }

    private suspend fun authorizeStoresSessionOnSuccess(
        provider: AuthProvider,
        isNewAccount: Boolean,
        accessToken: String,
        customerId: String,
    ) {
        val request = requestFor(provider)
        val response = testAuthResponse(
            accessToken = accessToken,
            customerId = customerId,
            provider = provider,
            isNewAccount = isNewAccount,
        )
        fakeSession.userId = null
        fakeSession.accessToken = null
        fakeSession.refreshToken = null
        fakeApi.authorizeResult = ApiResult.Success(response.toTestDto())

        val result = repository.authorize(request)

        assertEquals(ApiResult.Success(response), result)
        assertEquals(request.toDto(), fakeApi.lastAuthorizeRequest)
        assertEquals(customerId, fakeSession.userId)
        assertEquals(accessToken, fakeSession.accessToken)
        // Dropping the refresh token here would leave the session unable to survive the
        // first hour, and the failure would only show up long after sign-in.
        assertEquals(response.refreshToken, fakeSession.refreshToken)
    }

    private suspend fun authorizeDoesNotStoreSessionOnError(
        request: AuthRequest,
        error: NetworkError,
    ) {
        fakeSession.userId = null
        fakeSession.accessToken = null
        fakeSession.refreshToken = null
        fakeApi.authorizeResult = ApiResult.Error(error)

        val result = repository.authorize(request)

        assertEquals(ApiResult.Error(error), result)
        assertEquals(request.toDto(), fakeApi.lastAuthorizeRequest)
        assertNull(fakeSession.userId)
        assertNull(fakeSession.accessToken)
        assertNull(fakeSession.refreshToken)
    }

    // Production code never converts an AuthResponse back into a DTO (the client never
    // re-sends an auth response), so this mapper is test-only.
    private fun AuthResponse.toTestDto(): AuthResponseDto = AuthResponseDto(
        accessToken = accessToken,
        refreshToken = refreshToken,
        customer = customer.toDto(),
        isNewAccount = isNewAccount,
        provider = provider,
    )

    private fun requestFor(provider: AuthProvider): AuthRequest = when (provider) {
        AuthProvider.MANUAL -> manualRequest()
        AuthProvider.GOOGLE -> googleRequest()
        else -> error("Unsupported provider in test: $provider")
    }

    private fun manualRequest(password: String = "secret123") = AuthRequest(
        provider = AuthProvider.MANUAL,
        email = "user@gmail.com",
        password = password,
    )

    private fun googleRequest() = AuthRequest(
        provider = AuthProvider.GOOGLE,
        email = "user@gmail.com",
        providerUserId = "google-uid-123",
        displayName = "Misha Test",
    )

    private fun testAuthResponse(
        accessToken: String,
        customerId: String,
        provider: AuthProvider,
        isNewAccount: Boolean,
        refreshToken: String = "refresh-$accessToken",
    ) = AuthResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        customer = testCustomer.copy(id = customerId),
        isNewAccount = isNewAccount,
        provider = provider,
    )
}
