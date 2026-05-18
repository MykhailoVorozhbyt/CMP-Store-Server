package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.model.AuthUser
import com.feature.authentication.domain.model.CreateCustomerResult
import com.feature.authentication.domain.repository.CustomerRepository

class CreateCustomerUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(user: AuthUser?): CreateCustomerResult =
        repository.createCustomer(user)
}
