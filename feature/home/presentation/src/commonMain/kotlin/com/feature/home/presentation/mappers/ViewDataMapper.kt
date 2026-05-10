package com.feature.home.presentation.mappers

import com.feature.home.presentation.view_data.CartItemViewData
import com.feature.home.presentation.view_data.CustomerViewData
import com.feature.home.presentation.view_data.PhoneNumberViewData
import kotlinx.collections.immutable.toPersistentList
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

fun Customer.toViewData(): CustomerViewData = CustomerViewData(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    city = city,
    postalCode = postalCode,
    address = address,
    phoneNumber = phoneNumber?.toViewData(),
    cart = cart.map { it.toViewData() }.toPersistentList(),
    isAdmin = isAdmin,
)


fun CartItem.toViewData(): CartItemViewData = CartItemViewData(
    id = id,
    productId = productId,
    flavor = flavor,
    quantity = quantity,
)

fun PhoneNumber.toViewData(): PhoneNumberViewData = PhoneNumberViewData(
    dialCode = dialCode,
    number = number,
)