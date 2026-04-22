package com.store.core.presentation.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey

class Navigator(startDestination: NavKey) {
    val backStack = mutableStateListOf(startDestination)
}