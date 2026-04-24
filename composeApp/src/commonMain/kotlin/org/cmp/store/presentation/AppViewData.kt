package org.cmp.store.presentation

import org.cmp.store.navigation.Screen

data class AppViewData(
    val appReady: Boolean = false,
    val startDestination: Screen = Screen.Auth
)
