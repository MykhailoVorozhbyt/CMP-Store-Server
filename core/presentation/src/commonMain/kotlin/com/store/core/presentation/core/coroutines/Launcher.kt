package com.store.core.presentation.core.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches a new coroutine tied to the *owner*‐defined scope.
 *
 * Typical implementation simply forwards the call to
 * `scope.launch(ownerCoroutineContext + context, start, block)`, where
 * `ownerCoroutineContext` is a Owner-level dispatcher/supervisor context
 * and *context* is an optional per-call supplement (e.g. a different
 * dispatcher or additional elements).
 *
 * It is used to launch coroutines in a specific context with a given start option.
 */
interface Launcher {

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job
}

interface LauncherIO : Launcher {

    fun launchIo(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job
}