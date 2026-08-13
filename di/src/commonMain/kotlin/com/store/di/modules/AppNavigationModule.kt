package com.store.di.modules

import com.feature.home.presentation.NavigationPlaceholderScreen
import com.store.core.navigation.di.navEntry
import com.store.core.presentation.navigation.Screen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val appNavigationModule = module {
    navEntry(Screen.ContactUs.serializer()) {
        NavigationPlaceholderScreen("Products overview")
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
