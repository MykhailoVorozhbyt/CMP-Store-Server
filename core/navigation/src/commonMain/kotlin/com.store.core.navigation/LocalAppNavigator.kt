package com.store.core.navigation

import androidx.compose.runtime.compositionLocalOf

val LocalAppNavigator = compositionLocalOf<AppNavigator> {
    error("AppNavigator was not provided")
}
