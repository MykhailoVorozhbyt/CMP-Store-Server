package com.store.core.presentation.coroutines

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
 * `ownerCoroutineContext` is an Owner-level dispatcher/supervisor context
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

/**
 * Launches a coroutine ensuring **only one active job** for this launcher.
 *
 * The default contract is *“last call wins”*:
 * the previously running job (if any) is cancelled **before**
 * the new one starts. No extra checks are done against the *block*,
 * *context* or *start* parameters – *every* invocation fully replaces
 * the previous one with same `jobKey`.
 *
 * Most concrete implementations simply delegate to [SingleJobLaunch.Delegate]
 *
 * > **Caution:** using `launchSingle` in a shared component may
 * > inadvertently cancel work started by another caller.
 * > ⚠️ Use your own job keys to distinguish between different callers ⚠️
 */
interface SingleLauncher {

    fun launchSingle(
        context: CoroutineContext = EmptyCoroutineContext,
        jobKey: String = SingleJobLaunch.DEFAULT_JOB_KEY,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job

    fun cancelSingle(jobKey: String)

}