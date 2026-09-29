package com.store.component.customer.data

import com.store.component.customer.data.mapper.toDto
import com.store.component.customer.model.CustomerError
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.test.fakes.FakeLocalAuthSessionDataSource
import kotlinx.coroutines.test.runTest
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultCustomerRepositoryTest {
    private val fakeApi = FakeCustomerDataSource()
    private val fakeSession = FakeLocalAuthSessionDataSource()
    private val repository = DefaultCustomerRepository(fakeApi, fakeSession)

    @Test
    fun readCustomer_returnsSuccessWhenAuthenticatedAndApiSucceeds() = runTest {
        fakeSession.userId = "uid-1"
        fakeApi.getCustomerResult = ApiResult.Success(testCustomerDto)

        val result = repository.readCustomer()

        assertEquals(ApiResult.Success(testCustomer), result)
        assertEquals("uid-1", fakeApi.lastRequestedCustomerId)
    }

    @Test
    fun readCustomer_returnsUnauthorizedWhenNoSession() = runTest {
        fakeSession.userId = null

        val result = repository.readCustomer()

        assertEquals(ApiResult.Error(CustomerError.Common(DataError.Unauthorized)), result)
    }

    @Test
    fun readCustomer_mapsCustomerNotFoundToNotFound() = runTest {
        fakeSession.userId = "uid-1"
        fakeApi.getCustomerResult = ApiResult.Error(NetworkError.CUSTOMER_NOT_FOUND)

        val result = repository.readCustomer()

        assertEquals(ApiResult.Error(CustomerError.NotFound), result)
    }

    @Test
    fun updateCustomer_returnsSuccessWhenApiSucceeds() = runTest {
        val result = repository.updateCustomer(testCustomer)

        assertEquals(ApiResult.Success(Unit), result)
        assertEquals(testCustomer.toDto(), fakeApi.lastUpdatedCustomer)
    }

    @Test
    fun updateCustomer_mapsApiErrorToCommonError() = runTest {
        fakeApi.updateCustomerResult = ApiResult.Error(NetworkError.SERVER_ERROR)

        val result = repository.updateCustomer(testCustomer)

        assertEquals(ApiResult.Error(CustomerError.Common(DataError.Server)), result)
    }
}
