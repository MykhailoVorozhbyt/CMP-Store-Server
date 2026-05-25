package com.feature.authentication.data.mappers

import com.feature.authentication.data.model.CartItemDto
import com.feature.authentication.data.model.CustomerDto
import com.feature.authentication.data.model.PhoneNumberDto
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

fun PhoneNumberDto.toPhoneNumber(): PhoneNumber = PhoneNumber(
    dialCode = dialCode,
    number = number,
)

fun PhoneNumber.toDto(): PhoneNumberDto = PhoneNumberDto(
    dialCode = dialCode,
    number = number,
)

fun CartItemDto.toCartItem(): CartItem = CartItem(
    id = id,
    productId = productId,
    flavor = flavor,
    quantity = quantity,
)

fun CartItem.toDto(): CartItemDto = CartItemDto(
    id = id,
    productId = productId,
    flavor = flavor,
    quantity = quantity,
)

fun CustomerDto.toCustomer(): Customer = Customer(
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

fun Customer.toDto(): CustomerDto = CustomerDto(
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
