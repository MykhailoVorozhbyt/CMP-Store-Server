package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.CustomerRepository
import org.cmp.store.domain.customer.Customer
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

class UpdateCustomerUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): EmptyResult<NetworkError> =
        repository.updateCustomer(customer)
}
