package com.store.component.customer.data

import com.store.component.customer.data.data_source.CustomerDataSource
import com.store.component.customer.data.dto.CustomerDto
import com.store.component.customer.data.dto.CustomerProfileDto
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

internal class FakeCustomerDataSource : CustomerDataSource {
    var getCustomerResult: ApiResult<CustomerDto, NetworkError> = ApiResult.Error(NetworkError.UNKNOWN)
    var updateCustomerResult: EmptyResult<NetworkError> = ApiResult.Success(Unit)
    var lastRequestedCustomerId: String? = null
    var lastUpdatedCustomer: CustomerDto? = null
    var updateProfileResult: EmptyResult<NetworkError> = ApiResult.Success(Unit)
    var lastProfileCustomerId: String? = null
    var lastUpdatedProfile: CustomerProfileDto? = null

    override suspend fun getCustomer(id: String): ApiResult<CustomerDto, NetworkError> {
        lastRequestedCustomerId = id
        return getCustomerResult
    }

    override suspend fun updateCustomer(customer: CustomerDto): EmptyResult<NetworkError> {
        lastUpdatedCustomer = customer
        return updateCustomerResult
    }

    override suspend fun updateProfile(id: String, profile: CustomerProfileDto): EmptyResult<NetworkError> {
        lastProfileCustomerId = id
        lastUpdatedProfile = profile
        return updateProfileResult
    }
}
