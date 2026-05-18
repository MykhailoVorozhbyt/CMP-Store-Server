package com.store.core.navigation.di

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
inline fun <reified T : NavKey> Module.navEntry(
    serializer: KSerializer<T>,
    metadata: Map<String, Any> = emptyMap(),
    noinline definition: @Composable Scope.(T) -> Unit,
) {
    navKey(serializer)
    navigation(metadata, definition)
}

inline fun <reified T : NavKey> Module.navKey(
    serializer: KSerializer<T>,
): KoinDefinition<NavKeyProviderInstaller<T>> {
    return single<NavKeyProviderInstaller<T>>(named<T>()) {
        NavKeyProviderInstaller { it.subclass(T::class, serializer) }
    }
}
