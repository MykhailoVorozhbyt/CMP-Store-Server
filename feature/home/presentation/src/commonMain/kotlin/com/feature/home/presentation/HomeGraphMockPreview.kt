package com.feature.home.presentation

import com.feature.home.presentation.view_data.CartItemViewData
import com.feature.home.presentation.view_data.CustomerViewData
import com.feature.home.presentation.view_data.HomeGraphViewData
import com.feature.home.presentation.view_data.PhoneNumberViewData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

object HomeGraphMockPreview {
    fun getViewData() = HomeGraphViewData(
        isLoading = false,
    )

    fun getCustomer(isAdmin: Boolean = false) = CustomerViewData(
        id = "1",
        firstName = "User",
        lastName = "LasName",
        email = "email_test@gmail.com",
        city = "Barcelona",
        postalCode = 123123,
        address = "Camp Nou",
        phoneNumber = PhoneNumberViewData(1, "94124124124"),
        cart = getCartItems(),
        isAdmin = isAdmin,
    )

    fun getCartItems() = buildList {
        repeat(5) {
            add(getCartItem(it.toString()))
        }
    }.toImmutableList()

    fun getCartItem(id: String = "0") = CartItemViewData(
        id = id,
        productId = id,
        flavor = null,
        quantity = 1,
    )
}
