package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.CustomerRepository

class SignOutUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.signOut()
}
