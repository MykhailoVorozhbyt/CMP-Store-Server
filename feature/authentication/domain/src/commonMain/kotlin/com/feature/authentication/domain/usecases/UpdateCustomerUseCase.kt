package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.CustomerRepository
import dev.gitlive.firebase.auth.FirebaseUser
import org.cmp.store.domain.customer.Customer

class UpdateCustomerUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): Result<Unit> =
        repository.updateCustomer(customer)
}
