package com.store.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.cmp.store.navigation.Screen

class AppNavigator(
    private val backStack: NavBackStack<NavKey>,
) {

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    fun replaceAll(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }

    fun back() = backStack.removeLastOrNull()

}
