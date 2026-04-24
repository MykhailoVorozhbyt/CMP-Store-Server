package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.customer.Customer

class ReadCustomerUseCase(
    private val repository: CustomerRepository
) {
    operator fun invoke(): Flow<Result<Customer>> = repository.readCustomerFlow()
}
