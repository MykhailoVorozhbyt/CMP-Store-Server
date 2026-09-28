package com.store.component.customer.usecase

import com.store.component.customer.domain_api.CustomerRepository
import com.store.component.customer.model.CustomerError
import com.store.core.domain.EmptyResult
import org.cmp.store.domain.customer.Customer

class UpdateCustomerUseCase(
    private val repository: CustomerRepository,
) {
    suspend operator fun invoke(customer: Customer): EmptyResult<CustomerError> =
        repository.updateCustomer(customer)
}
