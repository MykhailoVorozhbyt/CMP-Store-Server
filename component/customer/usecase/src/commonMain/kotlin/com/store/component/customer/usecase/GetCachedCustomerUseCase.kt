package com.store.component.customer.usecase

import com.store.component.customer.domain_api.CustomerRepository
import org.cmp.store.domain.customer.Customer

class GetCachedCustomerUseCase(
    private val repository: CustomerRepository,
) {
    suspend operator fun invoke(): Customer? = repository.getCachedCustomer()
}
