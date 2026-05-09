package com.store.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.store.core.navigation.di.rememberKoinNavBackStack
import org.cmp.store.navigation.Screen
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SetupNavGraph(
    startDestination: Screen,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberKoinNavBackStack(startDestination)
    val navigator = remember(backStack) { AppNavigator(backStack) }

    CompositionLocalProvider(LocalAppNavigator provides navigator) {
        NavDisplay(
            modifier = modifier,
            backStack = backStack,
            onBack = navigator::back,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = koinEntryProvider(),
        )
    }
}
