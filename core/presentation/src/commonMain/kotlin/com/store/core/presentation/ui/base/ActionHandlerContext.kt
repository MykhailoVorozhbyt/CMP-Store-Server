package com.store.core.presentation.ui.base

import com.store.core.presentation.core.coroutines.Launcher
import com.store.core.presentation.core.coroutines.LauncherIO
import com.store.core.presentation.core.coroutines.ScopeProvider
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext


/**
 * Execution context that can be passed to viewModel helper objects, such as [ViewActionHandler].
 *
 * Combines:
 * * [UiEventEmitter] – publish UI‐level events
 * * [ScopeProvider]  – expose the owner’s [CoroutineScope]
 * * [Launcher] – helper APIs for coroutine start
 *
 * ### Usage guidelines
 * * **Prefer [launch] instead of grabbing the raw [scope].**
 *   Implementations such as [BaseActionHandleViewModel] already inject
 *   _error handling_ and a predefined dispatcher via [mainCoroutineCtx];
 *   re-using [launch] keeps those guarantees.
 *
 * * Access [scope] **only** if you can't use [launch] for some reason, **BUT**
 *
 * ⚠️ **ALWAYS MERGE YOUR CONTEXT WITH [mainCoroutineCtx] or [ioContext]!**
 *
 * For example: `scope.launch(mainCoroutineCtx + myCtx) { ... }`
 *
 * @property mainCoroutineCtx base coroutine context (exceptionHandler + dispatcher + supervisor)
 *                          supplied by the owner (e.g. ViewModel).
 * @see BaseActionHandleViewModel
 */
interface ActionHandlerContext : UiEventEmitter, ScopeProvider, Launcher, LauncherIO {
    val mainCoroutineCtx: CoroutineContext
    val ioContext: CoroutineContext
}