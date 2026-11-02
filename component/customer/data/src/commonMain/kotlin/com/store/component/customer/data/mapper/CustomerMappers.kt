package com.store.component.customer.data.mapper

import com.store.component.customer.data.dto.CartItemDto
import com.store.component.customer.data.dto.CustomerDto
import com.store.component.customer.data.dto.CustomerProfileDto
import com.store.component.customer.data.dto.PhoneNumberDto
import com.store.component.customer.model.CustomerProfileRequest
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

internal fun CustomerDto.toCustomer(): Customer = Customer(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    city = city,
    postalCode = postalCode,
    address = address,
    phoneNumber = phoneNumber?.toPhoneNumber(),
    cart = cart.map { it.toCartItem() },
    isAdmin = isAdmin,
)

internal fun Customer.toDto(): CustomerDto = CustomerDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    city = city,
    postalCode = postalCode,
    address = address,
    phoneNumber = phoneNumber?.toDto(),
    cart = cart.map { it.toDto() },
    isAdmin = isAdmin,
)

internal fun CustomerProfileRequest.toDto(): CustomerProfileDto = CustomerProfileDto(
    firstName = firstName,
    lastName = lastName,
    city = city,
    postalCode = postalCode,
    address = address,
    phoneNumber = phoneNumber?.toDto(),
)

private fun PhoneNumberDto.toPhoneNumber(): PhoneNumber = PhoneNumber(dialCode = dialCode, number = number)

private fun PhoneNumber.toDto(): PhoneNumberDto = PhoneNumberDto(dialCode = dialCode, number = number)

private fun CartItemDto.toCartItem(): CartItem = CartItem(
    id = id,
    productId = productId,
    flavor = flavor,
    quantity = quantity,
)

private fun CartItem.toDto(): CartItemDto = CartItemDto(
    id = id,
    productId = productId,
    flavor = flavor,
    quantity = quantity,
)
