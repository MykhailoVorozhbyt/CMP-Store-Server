package com.feature.authentication.domain.repository

import com.feature.authentication.domain.model.AuthUser
import com.feature.authentication.domain.model.CreateCustomerResult
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.customer.Customer

interface CustomerRepository {
    fun getCurrentUserId(): String?
    suspend fun createCustomer(user: AuthUser?): CreateCustomerResult
    fun readCustomerFlow(): Flow<Result<Customer>>
    suspend fun updateCustomer(customer: Customer): Result<Unit>
    suspend fun signOut(): Result<Unit>
}
