package com.store.di

import com.feature.authentication.presentation.AuthenticationScreen
import com.feature.home.presentation.HomeGraphScreen
import com.feature.home.presentation.NavigationPlaceholderScreen
import com.store.core.navigation.AppNavigator
import com.store.core.navigation.di.navEntry
import com.store.core.navigation.RootNavigator
import org.cmp.store.navigation.Screen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    singleOf(::AppNavigator)
    singleOf(::RootNavigator)

    navEntry(Screen.Auth.serializer()) {
        val navigator = get<AppNavigator>()
        AuthenticationScreen(
            navigateToHome = { m ->
                navigator.replaceAll(Screen.HomeGraph(m))
            }
        )
    }
    navEntry(Screen.HomeGraph.serializer()) {
        val appNavigator = get<AppNavigator>()
        HomeGraphScreen(
            welcomeMessage = it.welcomeMessage,
            rootNavigator = appNavigator,
        )
    }
    navEntry(Screen.ProductsOverview.serializer()) {
        NavigationPlaceholderScreen("Products overview")
    }
    navEntry(Screen.Cart.serializer()) {
        NavigationPlaceholderScreen("Cart")
    }
    navEntry(Screen.Categories.serializer()) {
        NavigationPlaceholderScreen("Categories")
    }
    navEntry(Screen.Profile.serializer()) {
        NavigationPlaceholderScreen("Profile")
    }
    navEntry(Screen.AdminPanel.serializer()) {
        NavigationPlaceholderScreen("Admin panel")
    }
    navEntry(Screen.Details.serializer()) {
        NavigationPlaceholderScreen("Details: ${it.id}")
    }
    navEntry(Screen.CategorySearch.serializer()) {
        NavigationPlaceholderScreen("Category: ${it.category}")
    }
    navEntry(Screen.Checkout.serializer()) {
        NavigationPlaceholderScreen("Checkout total: ${it.totalAmount}")
    }
}
