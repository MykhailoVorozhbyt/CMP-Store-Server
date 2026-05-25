package com.feature.authentication.data

import com.feature.authentication.domain.model.AuthUser
import com.feature.authentication.domain.model.CreateCustomerResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.cmp.store.domain.customer.Customer
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JvmCustomerRepositoryImplTest {
    private val repository = JvmCustomerRepositoryImpl()

    @Test
    fun `getCurrentUserId always returns null`() {
        assertNull(repository.getCurrentUserId())
    }

    @Test
    fun `createCustomer always returns Failure`() = runBlocking {
        val result =
            repository.createCustomer(AuthUser(uid = "user-1", displayName = null, email = null))
        assertTrue(result is CreateCustomerResult.Failure)
    }

    @Test
    fun `readCustomerFlow emits failure`() = runBlocking {
        val result = repository.readCustomerFlow().first()
        assertTrue(result.isFailure)
    }

    @Test
    fun `updateCustomer returns failure`() = runBlocking {
        val result = repository.updateCustomer(
            Customer(
                id = "id",
                firstName = "firstName",
                lastName = "lastName",
                email = "email",
            )
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `signOut returns failure`() = runBlocking {
        val result = repository.signOut()
        assertTrue(result.isFailure)
    }
}
