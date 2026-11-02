package com.store.component.customer.data

import com.store.component.customer.data.dto.CustomerDto
import com.store.component.customer.data.dto.CustomerProfileDto
import com.store.component.customer.data.dto.PhoneNumberDto
import com.store.component.customer.model.CustomerProfileRequest
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

internal val testCustomer = Customer(
    id = "customer-id",
    firstName = "First",
    lastName = "Last",
    email = "user@example.com",
)

internal val testProfileRequest = CustomerProfileRequest(
    firstName = "Updated",
    lastName = "Profile",
    city = "Lviv",
    postalCode = 79000,
    address = "Rynok 1",
    phoneNumber = PhoneNumber(dialCode = 380, number = "123456789"),
)

internal val testCustomerProfileDto = CustomerProfileDto(
    firstName = "Updated",
    lastName = "Profile",
    city = "Lviv",
    postalCode = 79000,
    address = "Rynok 1",
    phoneNumber = PhoneNumberDto(dialCode = 380, number = "123456789"),
)

internal val testCustomerDto = CustomerDto(
    id = "customer-id",
    firstName = "First",
    lastName = "Last",
    email = "user@example.com",
)
