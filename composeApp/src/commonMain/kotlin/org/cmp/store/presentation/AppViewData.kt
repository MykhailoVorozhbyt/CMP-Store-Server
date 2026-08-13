package org.cmp.store.presentation

import com.store.core.presentation.navigation.Screen

data class AppViewData(
    val appReady: Boolean = false,
    val startDestination: Screen = Screen.Auth
)
