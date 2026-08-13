package com.feature.home.presentation.utils

import androidx.navigation3.runtime.NavKey
import com.store.core.resources.Resources
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.store.core.presentation.navigation.Screen
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

internal val TOP_LEVEL_SCREENS: ImmutableList<NavKey> = persistentListOf(
    Screen.ProductsOverview,
    Screen.Cart,
    Screen.Categories,
)
