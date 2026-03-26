package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.CustomerRepository

class CreateCustomerUseCase(
    private val repository: CustomerRepository
) {
    operator fun invoke() {

    }
}