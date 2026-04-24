package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.model.CreateCustomerResult
import com.feature.authentication.domain.repository.CustomerRepository
import dev.gitlive.firebase.auth.FirebaseUser

class CreateCustomerUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(user: FirebaseUser?): CreateCustomerResult =
        repository.createCustomer(user)
}