package com.store.component.customer.usecase

import com.store.component.customer.domain_api.CustomerRepository
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.customer.Customer

class ObserveCustomerUseCase(
    private val repository: CustomerRepository,
) {
    operator fun invoke(): Flow<Customer?> = repository.observeCustomer()
}
