package com.store.di

import com.feature.authentication.presentation.AuthenticationScreen
import com.feature.home.presentation.HomeScreen
import com.store.core.navigation.navEntry
import com.store.core.presentation.navigation.Navigator
import org.cmp.store.navigation.Screen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    single { Navigator() }

    navEntry(Screen.Auth.serializer()) {
        val navigator = get<Navigator>()
        AuthenticationScreen(
            navigateToHome = { m ->
                navigator.backStack.remove(Screen.Auth)
                navigator.backStack.add(Screen.HomeGraph(m))
            }
        )
    }
    navEntry(Screen.HomeGraph.serializer()) {
        HomeScreen(
            welcomeMessage = it.welcomeMessage
        )
    }
}
