package com.store.component.customer.usecase

import com.store.component.customer.model.CustomerProfileRequest
import com.store.core.domain.ApiResult
import com.store.test.fakes.FakeCustomerRepository
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    @Test
    fun updateCustomerUseCase_with_profile_request_delegates_to_updateProfile() = runTest {
        val request = CustomerProfileRequest(
            firstName = "First",
            lastName = "Last",
            city = "Kyiv",
            postalCode = 1000,
            address = "Main street 1",
            phoneNumber = PhoneNumber(dialCode = 380, number = "123456789"),
        )

        val result = UpdateCustomerUseCase(repository)(request)

        assertEquals(ApiResult.Success(Unit), result)
        assertEquals(request, repository.lastProfileRequest)
        assertNull(repository.lastUpdatedCustomer)
    }

    private fun customer() = Customer(
        id = "customer-id",
        firstName = "First",
        lastName = "Last",
        email = "user@example.com",
    )
}
