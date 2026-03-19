package com.store.core.presentation.core.coroutines

import kotlinx.coroutines.CoroutineScope

interface ScopeProvider {
    val scope: CoroutineScope
}
