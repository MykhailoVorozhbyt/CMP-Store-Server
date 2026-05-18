package com.feature.authentication.data

import com.feature.authentication.data.fakes.FakeAuthSessionDataSource
import com.feature.authentication.data.fakes.FakeRemoteDataSource
import com.feature.authentication.data.models.testCustomer
import com.feature.authentication.domain.model.AuthUser
import com.feature.authentication.domain.model.CreateCustomerResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.cmp.store.network.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CustomerRepositoryImplTest {
    private val fakeApi = FakeRemoteDataSource()
    private val fakeAuth = FakeAuthSessionDataSource()
    private val repository = CustomerRepositoryImpl(fakeApi, fakeAuth)

    // --- getCurrentUserId ---

    @Test
    fun getCurrentUserId_returnsIdFromAuthSource() {
        fakeAuth.userId = "abc-123"
        assertEquals("abc-123", repository.getCurrentUserId())
    }

    @Test
    fun getCurrentUserId_returnsNullWhenNotAuthenticated() {
        fakeAuth.userId = null
        assertNull(repository.getCurrentUserId())
    }

    // --- createCustomer ---

    @Test
    fun createCustomer_returnsSuccessWhenApiSucceeds() = runBlocking {
        val user = AuthUser(
            uid = "uid-1",
            displayName = "John Doe",
            email = "j@test.com",
        )
        fakeApi.createCustomerResult = ApiResult.Success(Unit)

        val result = repository.createCustomer(user = user)

        assertEquals(CreateCustomerResult.Success, result)
    }

    @Test
    fun createCustomer_returnsFailureWhenUserIsNull() = runBlocking {
        val result = repository.createCustomer(user = null)
        assertTrue(result is CreateCustomerResult.Failure)
    }

    @Test
    fun createCustomer_returnsUserAlreadyExistsWhenApiReturnsThatError() = runBlocking {
        val user = AuthUser(
            uid = "uid-1",
            displayName = null,
            email = "j@test.com",
        )
        fakeApi.createCustomerResult = ApiResult.Error(NetworkError.USER_ALREADY_EXISTS)

        val result = repository.createCustomer(user = user)

        assertEquals(CreateCustomerResult.UserAlreadyExists, result)
    }

    // --- readCustomerFlow ---

    @Test
    fun readCustomerFlow_emitsSuccessWhenAuthenticatedAndApiSucceeds() = runBlocking {
        fakeAuth.userId = "uid-1"
        fakeApi.getCustomerResult = ApiResult.Success(testCustomer)

        val result = repository.readCustomerFlow().first()

        assertEquals(Result.success(testCustomer), result)
    }

    @Test
    fun readCustomerFlow_emitsFailureWhenNotAuthenticated() = runBlocking {
        fakeAuth.userId = null

        val result = repository.readCustomerFlow().first()

        assertTrue(result.isFailure)
    }

    // --- updateCustomer ---

    @Test
    fun updateCustomer_returnsSuccessWhenApiSucceeds() = runBlocking {
        fakeApi.updateCustomerResult = ApiResult.Success(Unit)
        val result = repository.updateCustomer(testCustomer)
        assertTrue(result.isSuccess)
    }

    @Test
    fun updateCustomer_returnsFailureWhenApiReturnsError() = runBlocking {
        fakeApi.updateCustomerResult = ApiResult.Error(NetworkError.UNKNOWN)
        val result = repository.updateCustomer(testCustomer)
        assertTrue(result.isFailure)
    }

    // --- signOut ---

    @Test
    fun signOut_callsAuthDataSourceAndReturnsSuccess() = runBlocking {
        val result = repository.signOut()
        assertTrue(fakeAuth.signOutCalled)
        assertTrue(result.isSuccess)
    }
}
