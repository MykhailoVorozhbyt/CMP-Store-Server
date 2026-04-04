package com.store.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.feature.authentication.presentation.AuthenticationScreen
import com.feature.authentication.presentation.HomeScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.cmp.store.navigation.Screen


@Composable
fun SetupNavGraph(modifier: Modifier = Modifier) {
    /*   //    val intentHandler = koinInject<IntentHandler>()
       //    val navigateTo by intentHandler.navigateTo.collectAsState()
       //
       //    LaunchedEffect(navigateTo) {
       //        navigateTo?.let { paymentCompleted ->
       //            navController.navigate(paymentCompleted)
       //            intentHandler.resetNavigation()
       //        }
       //    }

           val preferencesData by PreferencesRepository.readPayPalDataFlow()
               .collectAsState(initial = null)

           LaunchedEffect(preferencesData) {
               preferencesData?.let { paymentCompleted ->
                   if(paymentCompleted.token != null) {
                       navController.navigate(paymentCompleted)
                       PreferencesRepository.reset()
                   }
               }
           }*/
    val backStack = rememberNavBackStack(config, Screen.Auth)
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.Auth> {
                AuthenticationScreen(navigateToHome = {
                    backStack.remove(it)
                    backStack.add(Screen.HomeGraph)
                })
            }
            entry<Screen.HomeGraph> {
                HomeScreen()
            }
        }
    )
}


private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Screen.Auth::class, Screen.Auth.serializer())
            subclass(Screen.HomeGraph::class, Screen.HomeGraph.serializer())
        }
    }
}

