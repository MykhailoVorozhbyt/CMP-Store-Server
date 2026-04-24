package com.store.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.LocalKoinScopeContext
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope

@Composable
fun rememberKoinNavBackStack(vararg elements: NavKey): NavBackStack<NavKey> {
    return rememberNavBackStack(
        configuration = koinNavConfigProvider(),
        elements = elements,
    )
}

@OptIn(KoinInternalApi::class)
@Composable
private fun koinNavConfigProvider(
    scope: Scope = LocalKoinScopeContext.current.getValue(),
): SavedStateConfiguration {
    val entries = scope.getAll<NavKeyProviderInstaller<out NavKey>>()
    return SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                entries.forEach { it.build(this) }
            }
        }
    }
}
