package com.feature.home.presentation

import com.feature.home.presentation.view_data.HomeViewData
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber

object HomeMockPreview {
    fun getViewData() = HomeViewData(
        isLoading = false,
    )

    fun getCustomer(isAdmin: Boolean = false) = Customer(
        id = "1",
        firstName = "User",
        lastName = "LasName",
        email = "email_test@gmail.com",
        city = "Barcelona",
        postalCode = 123123,
        address = "Camp Nou",
        phoneNumber = PhoneNumber(1, "94124124124"),
        cart = getCartItems(),
        isAdmin = isAdmin,
    )

    fun getCartItems() = buildList {
        repeat(5) {
            add(getCartItem(it.toString()))
        }
    }

    fun getCartItem(id: String = "0") = CartItem(
        productId = id,
        flavor = null,
        quantity = 1,
    )
}
