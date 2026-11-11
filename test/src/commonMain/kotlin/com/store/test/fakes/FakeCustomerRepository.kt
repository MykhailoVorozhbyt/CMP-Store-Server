package com.store.test.fakes

import com.store.component.customer.domain_api.CustomerRepository
import com.store.component.customer.model.CustomerError
import com.store.component.customer.model.CustomerProfileRequest
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.core.domain.EmptyResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.cmp.store.domain.customer.Customer

class FakeCustomerRepository : CustomerRepository {
    var customerResult: ApiResult<Customer, CustomerError> =
        ApiResult.Error(CustomerError.Common(DataError.Unknown))
    var updateResult: EmptyResult<CustomerError> = ApiResult.Success(Unit)
    var lastUpdatedCustomer: Customer? = null
    var lastProfileRequest: CustomerProfileRequest? = null
    var profileRequestCount = 0
    var readCount = 0
    var updateGate: CompletableDeferred<Unit>? = null
    val cachedCustomer = MutableStateFlow<Customer?>(null)

    override suspend fun getCachedCustomer(): Customer? = cachedCustomer.value

    override fun observeCustomer(): Flow<Customer?> = cachedCustomer

    override suspend fun readCustomer(): ApiResult<Customer, CustomerError> {
        readCount++
        return customerResult
    }

    override suspend fun updateCustomer(customer: Customer): EmptyResult<CustomerError> {
        lastUpdatedCustomer = customer
        return updateResult
    }

    override suspend fun updateProfile(request: CustomerProfileRequest): EmptyResult<CustomerError> {
        lastProfileRequest = request
        profileRequestCount++
        updateGate?.await()
        return updateResult
    }
}
