package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.model.SignInResult
import com.feature.authentication.domain.model.request.AuthUserRequest
import com.store.test.fakes.FakeAuthRepository
import com.store.test.fakes.FakeCustomerRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import org.cmp.store.domain.customer.Customer
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class AuthenticationUseCasesTest {

    private val fakeCustomerRepository = FakeCustomerRepository()
    private val fakeAuthRepository = FakeAuthRepository()

    @Test
    fun signInUseCase_returns_Success_for_new_manual_account() = runTest {
        val repository = fakeAuthRepository.apply {
            authorizeResult = ApiResult.Success(authResponse(isNewAccount = true))
        }

        val result = SignInUseCase(repository)("user@example.com", "secret123")

        assertEquals(SignInResult.Success(true), result)
        assertEquals(
            AuthRequest(
                provider = AuthProvider.MANUAL,
                email = "user@example.com",
                password = "secret123"
            ),
            repository.lastAuthorizeRequest
        )
    }

    @Test
    fun signInUseCase_returns_AlreadyRegistered_for_existing_manual_account() = runTest {
        val repository = fakeAuthRepository.apply {
            authorizeResult = ApiResult.Success(authResponse(isNewAccount = false))
        }

        val result = SignInUseCase(repository)("user@example.com", "secret123")

        assertEquals(SignInResult.Success(false), result)
    }

    @Test
    fun signInUseCase_maps_invalid_credentials_error_to_Failure_with_message() = runTest {
        val repository = fakeAuthRepository.apply {
            authorizeResult = ApiResult.Error(NetworkError.INVALID_CREDENTIALS)
        }

        val result = SignInUseCase(repository)("user@example.com", "wrong")

        assertEquals(SignInResult.Failure(NetworkError.INVALID_CREDENTIALS), result)
    }

    @Test
    fun signInUseCase_returns_Failure_for_generic_manual_error() = runTest {
        val repository = fakeAuthRepository.apply {
            authorizeResult = ApiResult.Error(NetworkError.SERVER_ERROR)
        }

        val result = SignInUseCase(repository)("user@example.com", "secret123")

        assertEquals(SignInResult.Failure(NetworkError.SERVER_ERROR), result)
    }

    @Test
    fun signInUseCase_returns_Failure_when_social_user_email_is_null() = runTest {
        val repository = fakeAuthRepository

        val result = SignInUseCase(repository)(
            AuthUserRequest(uid = "uid-1", displayName = "Name", email = null)
        )

        assertEquals(SignInResult.Failure(NetworkError.EMAIL_REQUIRED), result)
        assertNull(repository.lastAuthorizeRequest)
    }

    @Test
    fun signInUseCase_returns_Failure_when_social_user_id_is_null() = runTest {
        val repository = fakeAuthRepository

        val result = SignInUseCase(repository)(
            AuthUserRequest(uid = null, displayName = "Name", email = "user@example.com")
        )

        assertEquals(SignInResult.Failure(NetworkError.PROVIDER_USER_ID_REQUIRED), result)
        assertNull(repository.lastAuthorizeRequest)
    }

    @Test
    fun signInUseCase_maps_social_request_and_returns_Success() = runTest {
        val repository = fakeAuthRepository.apply {
            authorizeResult =
                ApiResult.Success(authResponse(isNewAccount = true, provider = AuthProvider.GOOGLE))
        }

        val result = SignInUseCase(repository)(
            AuthUserRequest(
                uid = "google-uid",
                displayName = "Google User",
                email = "user@example.com"
            )
        )

        assertEquals(SignInResult.Success(true), result)
        assertEquals(
            AuthRequest(
                provider = AuthProvider.GOOGLE,
                email = "user@example.com",
                providerUserId = "google-uid",
                displayName = "Google User"
            ),
            repository.lastAuthorizeRequest
        )
    }

    @Test
    fun getCurrentUserIdUseCase_delegates_to_repository() = runTest {
        val repository = fakeCustomerRepository.apply { currentUserId = "uid-123" }

        val result = GetCurrentUserIdUseCase(repository)()

        assertEquals("uid-123", result)
    }

    @Test
    fun readCustomerUseCase_returns_repository_flow() {
        val customer = customer()
        val flow = flowOf(ApiResult.Success(customer))
        val repository = fakeCustomerRepository.apply { customerFlow = flow }

        val result = ReadCustomerUseCase(repository)()

        assertSame(flow, result)
    }

    @Test
    fun updateCustomerUseCase_delegates_to_repository() = runTest {
        val repository = fakeCustomerRepository.apply { updateResult = ApiResult.Success(Unit) }
        val customer = customer()

        val result = UpdateCustomerUseCase(repository)(customer)

        assertEquals(ApiResult.Success(Unit), result)
        assertEquals(customer, repository.lastUpdatedCustomer)
    }

    private fun authResponse(
        isNewAccount: Boolean,
        provider: AuthProvider = AuthProvider.MANUAL,
    ) = AuthResponse(
        accessToken = "token",
        refreshToken = "refresh-token",
        customer = customer(),
        isNewAccount = isNewAccount,
        provider = provider,
    )

    private fun customer() = Customer(
        id = "customer-id",
        firstName = "First",
        lastName = "Last",
        email = "user@example.com"
    )
}