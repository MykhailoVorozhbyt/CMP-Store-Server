package com.store.component.customer.data

import com.store.component.customer.data.dto.CustomerDto
import org.cmp.store.domain.customer.Customer

internal val testCustomer = Customer(
    id = "customer-id",
    firstName = "First",
    lastName = "Last",
    email = "user@example.com",
)

internal val testCustomerDto = CustomerDto(
    id = "customer-id",
    firstName = "First",
    lastName = "Last",
    email = "user@example.com",
)
