package com.store.component.customer.data

import com.store.component.customer.data.data_source.CustomerDataSource
import com.store.component.customer.data.mapper.toCustomer
import com.store.component.customer.data.mapper.toCustomerError
import com.store.component.customer.data.mapper.toDto
import com.store.component.customer.domain_api.CustomerRepository
import com.store.component.customer.model.CustomerError
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import com.store.core.domain.EmptyResult
import com.store.core.domain.mapError
import com.store.core.domain.mapSuccess
import com.store.core.security.LocalAuthSessionDataSource
import org.cmp.store.domain.customer.Customer

internal class DefaultCustomerRepository(
    private val api: CustomerDataSource,
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
) : CustomerRepository {

    override suspend fun readCustomer(): ApiResult<Customer, CustomerError> {
        val id = localAuthSessionDataSource.currentUserId()
            ?: return ApiResult.Error(CustomerError.Common(DataError.Unauthorized))
        return api.getCustomer(id)
            .mapSuccess { it.toCustomer() }
            .mapError { it.toCustomerError() }
    }

    override suspend fun updateCustomer(customer: Customer): EmptyResult<CustomerError> =
        api.updateCustomer(customer.toDto()).mapError { it.toCustomerError() }
}
