package com.feature.authentication.data.models

import com.feature.authentication.data.model.CustomerDto
import org.cmp.store.domain.customer.Customer

internal val testCustomer = Customer(
    id = "customer-id",
    firstName = "First",
    lastName = "Last",
    email = "user@example.com"
)

internal val testCustomerDto = CustomerDto(
    id = "customer-id",
    firstName = "First",
    lastName = "Last",
    email = "user@example.com"
)