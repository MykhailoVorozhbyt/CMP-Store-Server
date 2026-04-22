package com.store.di

import com.feature.authentication.presentation.AuthenticationScreen
import com.feature.authentication.presentation.HomeScreen
import com.store.core.presentation.navigation.Navigator
import org.cmp.store.navigation.Screen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    single { Navigator(startDestination = Screen.Auth) }

    navigation<Screen.Auth> {
        val navigator = get<Navigator>()
        AuthenticationScreen(
            navigateToHome = {
                navigator.backStack.remove(Screen.Auth)
                navigator.backStack.add(Screen.HomeGraph)
            }
        )
    }
    navigation<Screen.HomeGraph> {
        HomeScreen()
    }
}
