package com.feature.home.presentation

import androidx.navigation3.runtime.NavBackStack
import com.feature.home.presentation.view_data.CartItemViewData
import com.feature.home.presentation.view_data.CustomerViewData
import com.feature.home.presentation.view_data.HomeGraphViewData
import com.feature.home.presentation.view_data.PhoneNumberViewData
import com.store.core.navigation.NavigationState
import kotlinx.collections.immutable.toImmutableList
import com.store.core.presentation.navigation.Screen

object HomeGraphMockPreview {
    fun getNavigationState() = NavigationState(
        startKey = Screen.ProductsOverview,
        topLevelStack = NavBackStack(
            Screen.ProductsOverview,
            Screen.Cart,
            Screen.Categories
        ),
        subStacks = mapOf(),
    )

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
