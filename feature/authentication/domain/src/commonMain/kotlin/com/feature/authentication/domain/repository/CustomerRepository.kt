package com.feature.authentication.domain.repository

import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.customer.Customer

interface CustomerRepository {
    fun getCurrentUserId(): String?
    suspend fun createCustomer(user: FirebaseUser?): Result<Unit>
    fun readCustomerFlow(): Flow<Result<Customer>>
    suspend fun updateCustomer(customer: Customer): Result<Unit>
    suspend fun signOut(): Result<Unit>
}