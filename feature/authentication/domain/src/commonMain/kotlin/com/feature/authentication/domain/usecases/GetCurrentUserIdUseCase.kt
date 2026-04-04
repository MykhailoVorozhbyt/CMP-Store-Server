package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.CustomerRepository

class GetCurrentUserIdUseCase(
    private val repository: CustomerRepository
) {
    operator fun invoke(): String? = repository.getCurrentUserId()
}
