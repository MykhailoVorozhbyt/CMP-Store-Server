package com.store.component.customer.domain_api

import com.store.component.customer.model.CustomerError
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.domain.customer.Customer

interface CustomerRepository {
    suspend fun readCustomer(): ApiResult<Customer, CustomerError>
    suspend fun updateCustomer(customer: Customer): EmptyResult<CustomerError>
}
