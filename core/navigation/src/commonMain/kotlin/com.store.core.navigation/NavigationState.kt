package com.store.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.store.core.navigation.di.rememberKoinNavBackStack
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun rememberNavigationState(
    startKey: NavKey,
    topLevelKeys: List<NavKey>,
): NavigationState {
    val topLevelStack = rememberKoinNavBackStack(startKey)
    val topLevelKeysList = remember(topLevelKeys) { topLevelKeys.toList() }
    val subStackPairs = topLevelKeysList.map { key ->
        key to rememberKoinNavBackStack(key)
    }
    val subStacks = rememberMap(subStackPairs)

    return remember(startKey, topLevelKeysList, topLevelStack, subStacks) {
        NavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
    }
}

class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    val topLevelKeys: List<NavKey>
        get() = subStacks.keys
            .toList()

    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("Sub stack for $currentTopLevelKey does not exist")

    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavigationState.toEntries(
    entryProvider: (Any) -> NavEntry<Any> = koinEntryProvider(),
): SnapshotStateList<NavEntry<Any>> {
    val decoratedEntryPairs = subStacks.map { (key, backStack) ->
        rememberDecoratedNavEntries(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        ) to key
    }
    val decoratedEntries = rememberMap(
        decoratedEntryPairs.map { (entries, key) -> key to entries },
    )

    return topLevelStack
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}

@Composable
private fun <K, V> rememberMap(entries: List<Pair<K, V>>): Map<K, V> {
    val rememberKeys = entries.flatMap { (key, value) -> listOf(key as Any, value as Any) }
        .toTypedArray()
    return remember(*rememberKeys) {
        linkedMapOf<K, V>().apply {
            entries.forEach { (key, value) -> put(key, value) }
        }
    }
}
