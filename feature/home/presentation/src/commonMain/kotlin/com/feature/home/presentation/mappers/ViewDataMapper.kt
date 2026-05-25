package com.feature.home.presentation.mappers

import com.feature.home.presentation.view_data.CartItemViewData
import com.feature.home.presentation.view_data.CustomerViewData
import com.feature.home.presentation.view_data.PhoneNumberViewData
import com.store.core.presentation.utils.RequestState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber


fun RequestState<Customer>.toViewData(): RequestState<CustomerViewData> =
    when (this) {
        is RequestState.Success -> RequestState.Success(
            data.toViewData().copy(cart = data.cart.map { it.toViewData() }.toImmutableList())
        )

        is RequestState.Error -> RequestState.Error(message)
        RequestState.Idle -> RequestState.Idle
        RequestState.Loading -> RequestState.Loading
    }

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