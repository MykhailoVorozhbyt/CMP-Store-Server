package com.store.component.customer.data

import com.store.component.customer.data.mapper.toDto
import com.store.component.customer.model.CustomerError
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.test.fakes.FakeLocalAuthSessionDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CustomerRepositoryImplTest {
    private val fakeApi = FakeCustomerDataSource()
    private val fakeSession = FakeLocalAuthSessionDataSource()
    private val fakeLocal = FakeCustomerLocalDataSource()
    private val repository = CustomerRepositoryImpl(fakeApi, fakeLocal, fakeSession)

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

    @Test
    fun updateProfile_sendsProfileForSessionUser() = runTest {
        fakeSession.userId = "uid-1"

        val result = repository.updateProfile(testProfileRequest)

        assertEquals(ApiResult.Success(Unit), result)
        assertEquals("uid-1", fakeApi.lastProfileCustomerId)
        assertEquals(testCustomerProfileDto, fakeApi.lastUpdatedProfile)
    }

    @Test
    fun updateProfile_returnsUnauthorizedWithoutSession() = runTest {
        fakeSession.userId = null

        val result = repository.updateProfile(testProfileRequest)

        assertEquals(ApiResult.Error(CustomerError.Common(DataError.Unauthorized)), result)
        assertNull(fakeApi.lastUpdatedProfile)
    }

    @Test
    fun updateProfile_mapsCustomerNotFound() = runTest {
        fakeSession.userId = "uid-1"
        fakeApi.updateProfileResult = ApiResult.Error(NetworkError.CUSTOMER_NOT_FOUND)

        val result = repository.updateProfile(testProfileRequest)

        assertEquals(ApiResult.Error(CustomerError.NotFound), result)
    }

    @Test
    fun readCustomer_storesTheFreshCustomerInTheCache() = runTest {
        fakeSession.userId = testCustomer.id
        fakeApi.getCustomerResult = ApiResult.Success(testCustomerDto)

        repository.readCustomer()

        assertEquals(testCustomer, fakeLocal.stored.value)
    }

    @Test
    fun readCustomer_keepsTheCacheWhenOffline() = runTest {
        fakeSession.userId = testCustomer.id
        fakeLocal.stored.value = testCustomer
        fakeApi.getCustomerResult = ApiResult.NoInternet

        repository.readCustomer()

        assertEquals(testCustomer, fakeLocal.stored.value)
    }

    @Test
    fun getCachedCustomer_returnsTheCustomerOfTheCurrentSession() = runTest {
        fakeSession.userId = testCustomer.id
        fakeLocal.stored.value = testCustomer

        assertEquals(testCustomer, repository.getCachedCustomer())
    }

    @Test
    fun getCachedCustomer_hidesACustomerFromAnotherSession() = runTest {
        fakeSession.userId = "another-user"
        fakeLocal.stored.value = testCustomer

        assertNull(repository.getCachedCustomer())
    }

    @Test
    fun getCachedCustomer_returnsNullWithoutSession() = runTest {
        fakeSession.userId = null
        fakeLocal.stored.value = testCustomer

        assertNull(repository.getCachedCustomer())
    }

    @Test
    fun observeCustomer_hidesACustomerFromAnotherSession() = runTest {
        fakeSession.userId = "another-user"
        fakeLocal.stored.value = testCustomer

        assertNull(repository.observeCustomer().first())
    }

    @Test
    fun updateCustomer_writesTheCustomerToTheCacheOnSuccess() = runTest {
        repository.updateCustomer(testCustomer)

        assertEquals(testCustomer, fakeLocal.stored.value)
    }

    @Test
    fun updateCustomer_leavesTheCacheOnFailure() = runTest {
        fakeApi.updateCustomerResult = ApiResult.Error(NetworkError.SERVER_ERROR)

        repository.updateCustomer(testCustomer)

        assertNull(fakeLocal.stored.value)
    }

    @Test
    fun updateProfile_appliesTheProfileToTheCachedCustomer() = runTest {
        fakeSession.userId = testCustomer.id
        fakeLocal.stored.value = testCustomer

        repository.updateProfile(testProfileRequest)

        val cached = fakeLocal.stored.value
        assertEquals(testProfileRequest.firstName, cached?.firstName)
        assertEquals(testProfileRequest.city, cached?.city)
        assertEquals(testProfileRequest.phoneNumber, cached?.phoneNumber)
        assertEquals(testCustomer.email, cached?.email)
    }

    @Test
    fun updateProfile_leavesTheCacheWhenTheServerRejectsIt() = runTest {
        fakeSession.userId = testCustomer.id
        fakeLocal.stored.value = testCustomer
        fakeApi.updateProfileResult = ApiResult.Error(NetworkError.SERVER_ERROR)

        repository.updateProfile(testProfileRequest)

        assertEquals(testCustomer, fakeLocal.stored.value)
    }
}
