package com.feature.home.presentation.utils

import com.store.core.resources.Resources
import org.cmp.store.navigation.Screen
import org.jetbrains.compose.resources.DrawableResource

enum class BottomBarDestination(
    val icon: DrawableResource,
    val title: String,
    val screen: Screen
) {
    ProductsOverview(
        icon = Resources.Icon.Home,
        title = "Home",
        screen = Screen.ProductsOverview
    ),
    Cart(
        icon = Resources.Icon.ShoppingCart,
        title = "Cart",
        screen = Screen.Cart
    ),
    Categories(
        icon = Resources.Icon.Categories,
        title = "Categories",
        screen = Screen.Categories
    )
}

internal val TOP_LEVEL_SCREENS = setOf(
    Screen.ProductsOverview,
    Screen.Cart,
    Screen.Categories,
)