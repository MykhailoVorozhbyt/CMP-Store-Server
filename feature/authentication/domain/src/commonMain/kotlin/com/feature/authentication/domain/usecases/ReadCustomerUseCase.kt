package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.customer.Customer
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError

class ReadCustomerUseCase(
    private val repository: CustomerRepository
) {
    operator fun invoke(): Flow<ApiResult<Customer, NetworkError>> = repository.readCustomerFlow()
}
