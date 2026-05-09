package com.feature.authentication.data

import com.feature.authentication.domain.model.CreateCustomerResult
import com.feature.authentication.domain.repository.CustomerRepository
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.cmp.store.domain.customer.Customer

private const val DESKTOP_AUTH_MESSAGE = "Google/Firebase auth is not configured for desktop JVM."

class JvmCustomerRepositoryImpl : CustomerRepository {
    override fun getCurrentUserId(): String? = null

    override suspend fun createCustomer(user: FirebaseUser?): CreateCustomerResult {
        return CreateCustomerResult.Failure(DESKTOP_AUTH_MESSAGE)
    }

    override fun readCustomerFlow(): Flow<Result<Customer>> {
        return flowOf(Result.failure(IllegalStateException(DESKTOP_AUTH_MESSAGE)))
    }

    override suspend fun updateCustomer(customer: Customer): Result<Unit> {
        return Result.failure(IllegalStateException(DESKTOP_AUTH_MESSAGE))
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.failure(IllegalStateException(DESKTOP_AUTH_MESSAGE))
    }
}
