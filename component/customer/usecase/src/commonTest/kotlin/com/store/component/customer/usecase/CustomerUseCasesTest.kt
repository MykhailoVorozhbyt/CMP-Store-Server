package com.store.component.customer.usecase

import com.store.core.domain.ApiResult
import com.store.test.fakes.FakeCustomerRepository
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.customer.Customer
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomerUseCasesTest {

    private val repository = FakeCustomerRepository()

    @Test
    fun readCustomerUseCase_returns_repository_result() = runTest {
        repository.customerResult = ApiResult.Success(customer())

        assertEquals(ApiResult.Success(customer()), ReadCustomerUseCase(repository)())
    }

    @Test
    fun updateCustomerUseCase_delegates_to_repository() = runTest {
        val customer = customer()

        val result = UpdateCustomerUseCase(repository)(customer)

        assertEquals(ApiResult.Success(Unit), result)
        assertEquals(customer, repository.lastUpdatedCustomer)
    }

    private fun customer() = Customer(
        id = "customer-id",
        firstName = "First",
        lastName = "Last",
        email = "user@example.com",
    )
}
