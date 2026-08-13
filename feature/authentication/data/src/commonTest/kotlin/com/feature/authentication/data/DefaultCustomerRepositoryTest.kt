package com.feature.authentication.data

import com.store.test.fakes.FakeLocalAuthSessionDataSource
import com.feature.authentication.data.fakes.FakeCustomerDataSource
import com.feature.authentication.data.mappers.toDto
import com.feature.authentication.data.models.testCustomer
import com.feature.authentication.data.models.testCustomerDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultCustomerRepositoryTest {
    private val fakeApi = FakeCustomerDataSource()
    private val fakeAuth = FakeLocalAuthSessionDataSource()
    private val repository = DefaultCustomerRepository(fakeApi, fakeAuth)

    // --- getCurrentUserId ---
    @Test
    fun getCurrentUserId_returnsIdFromAuthSource() = runTest {
        fakeAuth.userId = "abc-123"
        assertEquals("abc-123", repository.getCurrentUserId())
    }

    @Test
    fun getCurrentUserId_returnsNullWhenNotAuthenticated() = runTest {
        fakeAuth.userId = null
        assertNull(repository.getCurrentUserId())
    }

    // --- getCurrentAccessToken ---
    @Test
    fun getCurrentAccessToken_returnsValueFromSessionSource() = runTest {
        fakeAuth.accessToken = "access-token"
        val result = repository.getCurrentAccessToken()
        assertEquals("access-token", result)
    }

    @Test
    fun getCurrentAccessToken_returnsNull() = runTest {
        fakeAuth.accessToken = null
        val result = repository.getCurrentAccessToken()
        assertEquals(null, result)
    }

    // --- readCustomerFlow ---
    @Test
    fun readCustomerFlow_emitsSuccessWhenAuthenticatedAndApiSucceeds() = runTest {
        fakeAuth.userId = "uid-1"
        fakeApi.getCustomerResult = ApiResult.Success(testCustomerDto)
        val result = repository.readCustomerFlow().first()
        assertEquals(ApiResult.Success(testCustomer), result)
        assertEquals("uid-1", fakeApi.lastRequestedCustomerId)
    }

    @Test
    fun readCustomerFlow_emitsFailureWhenNotAuthenticated() = runTest {
        fakeAuth.userId = null
        val result = repository.readCustomerFlow().first()
        assertEquals(ApiResult.Error(NetworkError.UNAUTHORIZED), result)
    }

    // --- updateCustomer ---
    @Test
    fun updateCustomer_returnsSuccessWhenApiSucceeds() = runTest {
        fakeApi.updateCustomerResult = ApiResult.Success(Unit)
        val result = repository.updateCustomer(testCustomer)
        assertEquals(ApiResult.Success(Unit), result)
        assertEquals(testCustomer.toDto(), fakeApi.lastUpdatedCustomer)
    }

    @Test
    fun updateCustomer_returnsFailureWhenApiReturnsError() = runTest {
        fakeApi.updateCustomerResult = ApiResult.Error(NetworkError.UNKNOWN)
        val result = repository.updateCustomer(testCustomer)
        assertEquals(ApiResult.Error(NetworkError.UNKNOWN), result)
    }

    @Test
    fun readCustomerFlow_emitsFailureWhenApiFails() = runTest {
        fakeAuth.userId = "uid-1"
        fakeApi.getCustomerResult = ApiResult.Error(NetworkError.CUSTOMER_NOT_FOUND)
        val result = repository.readCustomerFlow().first()
        assertEquals(ApiResult.Error(NetworkError.CUSTOMER_NOT_FOUND), result)
    }
}
