package com.store.component.customer.data

import com.store.component.customer.data.data_source.CustomerDataSource
import com.store.component.customer.data.data_source.CustomerLocalDataSource
import com.store.component.customer.data.mapper.toCustomer
import com.store.component.customer.data.mapper.toCustomerError
import com.store.component.customer.data.mapper.toDto
import com.store.component.customer.data.mapper.withProfile
import com.store.component.customer.domain_api.CustomerRepository
import com.store.component.customer.model.CustomerError
import com.store.component.customer.model.CustomerProfileRequest
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.core.domain.EmptyResult
import com.store.core.domain.mapError
import com.store.core.domain.mapSuccess
import com.store.core.security.LocalAuthSessionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cmp.store.domain.customer.Customer

internal class CustomerRepositoryImpl(
    private val api: CustomerDataSource,
    private val local: CustomerLocalDataSource,
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
) : CustomerRepository {

    override suspend fun getCachedCustomer(): Customer? =
        local.getCustomer()?.belongingToSession()

    override fun observeCustomer(): Flow<Customer?> =
        local.observeCustomer().map { it?.belongingToSession() }

    override suspend fun readCustomer(): ApiResult<Customer, CustomerError> {
        val id = localAuthSessionDataSource.currentUserId()
            ?: return ApiResult.Error(CustomerError.Common(DataError.Unauthorized))
        val result = api.getCustomer(id)
            .mapSuccess { it.toCustomer() }
            .mapError { it.toCustomerError() }
        when (result) {
            is ApiResult.Success -> local.update(result.data)
            is ApiResult.Error -> if (invalidatesCache(result.error)) local.clear()
            ApiResult.NoInternet -> Unit
        }
        return result
    }

    override suspend fun updateCustomer(customer: Customer): EmptyResult<CustomerError> {
        val result = api.updateCustomer(customer.toDto()).mapError { it.toCustomerError() }
        if (result is ApiResult.Success) local.update(customer)
        return result
    }

    override suspend fun updateProfile(request: CustomerProfileRequest): EmptyResult<CustomerError> {
        val id = localAuthSessionDataSource.currentUserId()
            ?: return ApiResult.Error(CustomerError.Common(DataError.Unauthorized))
        val result = api.updateProfile(id, request.toDto()).mapError { it.toCustomerError() }
        if (result is ApiResult.Success) {
            getCachedCustomer()?.let { cached -> local.update(cached.withProfile(request)) }
        }
        return result
    }

    private suspend fun Customer.belongingToSession(): Customer? =
        takeIf { it.id == localAuthSessionDataSource.currentUserId() }

    private fun invalidatesCache(error: CustomerError): Boolean {
        // TODO(human): decide which read errors make the cached customer untrustworthy
        return false
    }
}
