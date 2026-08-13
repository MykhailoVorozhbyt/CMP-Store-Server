package com.store.test.fakes

import com.feature.authentication.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.cmp.store.domain.customer.Customer
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

/**
 * Shared test double for [CustomerRepository]. All behaviour is driven by mutable properties so a
 * test can configure only what it needs and leave the rest as inert defaults.
 */
class FakeCustomerRepository : CustomerRepository {
    var currentUserId: String? = null
    var currentAccessToken: String? = null
    var customerFlow: Flow<ApiResult<Customer, NetworkError>> =
        flowOf(ApiResult.Error(NetworkError.UNKNOWN))
    var updateResult: EmptyResult<NetworkError> = ApiResult.Success(Unit)
    var lastUpdatedCustomer: Customer? = null

    override suspend fun getCurrentUserId(): String? = currentUserId

    override suspend fun getCurrentAccessToken(): String? = currentAccessToken

    override fun readCustomerFlow(): Flow<ApiResult<Customer, NetworkError>> = customerFlow

    override suspend fun updateCustomer(customer: Customer): EmptyResult<NetworkError> {
        lastUpdatedCustomer = customer
        return updateResult
    }
}
