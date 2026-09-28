package com.store.component.customer.usecase

import com.store.component.customer.domain_api.CustomerRepository
import com.store.component.customer.model.CustomerError
import com.store.core.domain.ApiResult
import org.cmp.store.domain.customer.Customer

class ReadCustomerUseCase(
    private val repository: CustomerRepository,
) {
    suspend operator fun invoke(): ApiResult<Customer, CustomerError> = repository.readCustomer()
}
