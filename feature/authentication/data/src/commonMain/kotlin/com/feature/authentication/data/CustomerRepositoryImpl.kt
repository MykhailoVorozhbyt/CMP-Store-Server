package com.feature.authentication.data

import com.feature.authentication.domain.repository.CustomerRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.cmp.store.domain.customer.Customer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CustomerRepositoryImpl(private val api: CustomerApi) : CustomerRepository {

    override fun getCurrentUserId(): String? = Firebase.auth.currentUser?.uid

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createCustomer(user: FirebaseUser?): Result<Unit> = runCatching {
//        val nameParts = user?.displayName?.split(" ") ?: emptyList()
//        val customer = Customer(
//            id = user?.uid ?: error("User ID is null"),
//            firstName = nameParts.firstOrNull() ?: "",
//            lastName = nameParts.drop(1).joinToString(" "),
//            email = user.email ?: ""
//        )
        val customer = Customer(
            id = Uuid.random().toString(),
            firstName = "Misha",
            lastName = "vorozh",
            email = user?.email ?: "user@gmail.com"
        )
        api.createCustomer(customer)
    }

    override fun readCustomerFlow(): Flow<Result<Customer>> = flow {
        emit(runCatching {
            val id = getCurrentUserId() ?: error("Not authenticated")
            api.getCustomer(id)
        })
    }

    override suspend fun updateCustomer(customer: Customer): Result<Unit> = runCatching {
        api.updateCustomer(customer)
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        Firebase.auth.signOut()
    }
}
