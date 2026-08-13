package com.feature.authentication.data.fakes

import com.feature.authentication.data.data_source.CustomerDataSource
import com.feature.authentication.data.model.CustomerDto
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

class FakeCustomerDataSource : CustomerDataSource {
    var getCustomerResult: ApiResult<CustomerDto, NetworkError> =
        ApiResult.Error(NetworkError.UNKNOWN)
    var updateCustomerResult: EmptyResult<NetworkError> = ApiResult.Success(Unit)
    var lastRequestedCustomerId: String? = null
    var lastUpdatedCustomer: CustomerDto? = null

    override suspend fun getCustomer(id: String): ApiResult<CustomerDto, NetworkError> {
        lastRequestedCustomerId = id
        return getCustomerResult
    }

    override suspend fun updateCustomer(customer: CustomerDto): EmptyResult<NetworkError> {
        lastUpdatedCustomer = customer
        return updateCustomerResult
    }
}
