package com.feature.authentication.data

import com.feature.authentication.domain.model.AuthUser
import com.feature.authentication.domain.model.CreateCustomerResult
import com.feature.authentication.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.cmp.store.domain.customer.Customer
import org.cmp.store.network.ApiResult
import org.cmp.store.network.NetworkError

class CustomerRepositoryImpl(
    private val api: RemoteDataSource,
    private val authSessionDataSource: AuthSessionDataSource,
) : CustomerRepository {

    override fun getCurrentUserId(): String? = authSessionDataSource.currentUserId()

    override suspend fun createCustomer(user: AuthUser?): CreateCustomerResult {
        val nameParts = user?.displayName?.split(" ") ?: emptyList()
        val customer = Customer(
            id = user?.uid ?: return CreateCustomerResult.Failure("User ID is null!"),
            firstName = nameParts.firstOrNull() ?: "",
            lastName = nameParts.drop(1).joinToString(" "),
            email = user.email ?: return CreateCustomerResult.Failure("User Email is null!"),
        )
        return when (val result = api.createCustomer(customer)) {
            is ApiResult.Success -> CreateCustomerResult.Success
            is ApiResult.Error -> when (result.error) {
                NetworkError.USER_ALREADY_EXISTS -> CreateCustomerResult.UserAlreadyExists
                else -> CreateCustomerResult.Failure(result.error.message)
            }
        }

    }

    override fun readCustomerFlow(): Flow<Result<Customer>> = flow {
        val id = getCurrentUserId()
        if (id == null) {
            emit(Result.failure(IllegalStateException("Not authenticated")))
            return@flow
        }
        when (val result = api.getCustomer(id)) {
            is ApiResult.Success -> emit(Result.success(result.data))
            is ApiResult.Error -> emit(Result.failure(Exception(result.error.message)))
        }
    }

    override suspend fun updateCustomer(customer: Customer): Result<Unit> =
        when (val result = api.updateCustomer(customer)) {
            is ApiResult.Success -> Result.success(Unit)
            is ApiResult.Error -> Result.failure(Exception(result.error.message))
        }

    override suspend fun signOut(): Result<Unit> = runCatching {
        authSessionDataSource.signOut()
    }
}
