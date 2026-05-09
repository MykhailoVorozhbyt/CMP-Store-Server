package com.feature.authentication.data.fakes

import com.feature.authentication.data.RemoteDataSource
import org.cmp.store.domain.customer.Customer
import org.cmp.store.network.ApiResult
import org.cmp.store.network.EmptyResult
import org.cmp.store.network.NetworkError

class FakeRemoteDataSource : RemoteDataSource {
    var createCustomerResult: EmptyResult<NetworkError> = ApiResult.Success(Unit)
    var getCustomerResult: ApiResult<Customer, NetworkError> = ApiResult.Success(
        Customer(
            id = "id",
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
        )
    )
    var updateCustomerResult: EmptyResult<NetworkError> = ApiResult.Success(Unit)

    override suspend fun createCustomer(customer: Customer) = createCustomerResult
    override suspend fun getCustomer(id: String) = getCustomerResult
    override suspend fun updateCustomer(customer: Customer) = updateCustomerResult
}