package com.store.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.cmp.store.navigation.Screen

class AppNavigator {
    private var backStack: NavBackStack<NavKey> = NavBackStack()

    fun attach(backStack: NavBackStack<NavKey>) {
        this.backStack = backStack
    }

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    fun replaceAll(screen: Screen) {
        while (backStack.removeLastOrNull() != null) {
        }
        backStack.add(screen)
    }

    fun back() {
        backStack.removeLastOrNull()
    }
}
