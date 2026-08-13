package com.store.test

import com.store.core.presentation.core.di.coroutines.AppDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Shared base for ViewModel unit tests. Adapted from the team's `tool/BaseTest` pattern, but for
 * KMP `commonTest`: no JUnit, no `ErrorLogger` — just deterministic coroutine plumbing.
 *
 * Provides:
 * - a single [scheduler] + [dispatcher] ([StandardTestDispatcher]) for full control over virtual time
 *   (`advanceUntilIdle()`, `runCurrent()`),
 * - [dispatchers] wired so every `launchIo` / default work runs on that same scheduler,
 * - automatic `Dispatchers.setMain` / `resetMain` around each test (so `viewModelScope` uses the
 *   test dispatcher),
 * - [runVmTest] which runs the body inside `runTest` bound to the same scheduler.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModelTest {

    val scheduler = TestCoroutineScheduler()
    val dispatcher = StandardTestDispatcher(scheduler)

    /**
     * `main` stays the delegating [Dispatchers.Main] singleton (redirected via `setMain`); io/default/
     * unconfined all funnel onto the single test [dispatcher] so nothing escapes the scheduler.
     */
    val dispatchers = AppDispatchers(
        io = dispatcher,
        default = dispatcher,
        unconfined = dispatcher,
    )

    @BeforeTest
    fun baseSetUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun baseTearDown() {
        Dispatchers.resetMain()
    }

    fun runVmTest(testBody: suspend TestScope.() -> Unit) =
        runTest(dispatcher) { testBody() }
}
