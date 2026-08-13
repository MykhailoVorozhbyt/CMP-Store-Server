package com.store.core.presentation.core.viewmodel

import androidx.lifecycle.viewModelScope
import com.store.core.presentation.core.NotificationType
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.base.MessageEventData
import com.store.core.presentation.ui.base.UiEvent
import com.store.test.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock

/**
 * Unit tests for [BaseActionHandleViewModel]. Runs on the shared [com.store.test.BaseViewModelTest] plumbing:
 * a single [scheduler] drives virtual time and every VM dispatcher points at it, so the debounce
 * windows (which read `timeProvider = { scheduler.currentTime }`) are fully deterministic.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseActionHandleViewModelTest : BaseViewModelTest() {

    private data class TestViewData(val counter: Int = 0)
    private data class TestViewAction(val id: Int = 0) : ViewAction

    private data class TestMessageEventData(val text: String) : MessageEventData {
        override val message: String get() = text
        override val type: NotificationType get() = NotificationType.INFO
    }

    /** A non-[ShowMessage] event carrying a distinct payload, used for ordering tests. */
    private data class TestPageEvent(val url: String) : UiEvent

    /** VM that records the id of every handled action. */
    private fun recordingVm(handled: MutableList<Int>) =
        object : BaseActionHandleViewModel<TestViewData>(
            dispatchers,
            timeProvider = { scheduler.currentTime },
        ) {
            override val _viewData = MutableStateFlow(TestViewData())
            override suspend fun handleViewAction(action: ViewAction) {
                handled += (action as TestViewAction).id
            }
        }

    /** VM that increments [TestViewData.counter] on every handled action. */
    private fun statefulVm() =
        object : BaseActionHandleViewModel<TestViewData>(
            dispatchers,
            timeProvider = { scheduler.currentTime },
        ) {
            override val _viewData = MutableStateFlow(TestViewData())
            override suspend fun handleViewAction(action: ViewAction) {
                _viewData.value = _viewData.value.copy(counter = _viewData.value.counter + 1)
            }
        }

    /** VM whose action handling is a no-op (used for pure UI-event tests). */
    private fun noopVm() =
        object : BaseActionHandleViewModel<TestViewData>(
            dispatchers,
            timeProvider = { scheduler.currentTime },
        ) {
            override val _viewData = MutableStateFlow(TestViewData())
            override suspend fun handleViewAction(action: ViewAction) {}
        }

    // region ViewAction dispatch

    @Test
    fun onViewAction_triggers_handleViewAction() = runVmTest {
        val handled = mutableListOf<Int>()
        val vm = recordingVm(handled)

        vm.onViewAction(TestViewAction())
        scheduler.advanceUntilIdle()

        assertEquals(1, handled.size)
    }

    // VA-00: Duplicate actions inside the throttle window are dropped
    @Test
    fun duplicate_view_actions_within_throttle_period_are_debounced() = runVmTest {
        val handled = mutableListOf<Int>()
        val vm = recordingVm(handled)

        vm.onViewAction(TestViewAction(0))
        vm.onViewAction(TestViewAction(0)) // same payload, same instant -> dropped
        scheduler.advanceTimeBy(500)
        scheduler.runCurrent()
        assertEquals(1, handled.size)

        vm.onViewAction(TestViewAction(0)) // same payload, past the window -> processed
        scheduler.advanceTimeBy(500)
        scheduler.runCurrent()
        assertEquals(2, handled.size)
    }

    // VA-01: Queue is not overflown with a burst of actions
    @Test
    fun all_view_actions_are_processed_in_order_for_burst() = runVmTest {
        val count = 10_000
        val handled = mutableListOf<Int>()
        val vm = recordingVm(handled)

        repeat(count) { vm.onViewAction(TestViewAction(it)) }
        scheduler.advanceUntilIdle()

        assertEquals(count, handled.size)
        assertEquals((0 until count).toList(), handled)
    }

    // VA-02: Throttle resets for different actions
    @Test
    fun throttle_resets_for_different_actions() = runVmTest {
        val handled = mutableListOf<Int>()
        val vm = recordingVm(handled)

        vm.onViewAction(TestViewAction(1))
        scheduler.advanceTimeBy(50) // < throttle window
        vm.onViewAction(TestViewAction(2)) // different id
        scheduler.advanceTimeBy(500)

        assertEquals(listOf(1, 2), handled)
    }

    // VA-04: Duplicate after the throttle window is processed again
    @Test
    fun duplicate_action_after_throttle_window_is_processed() = runVmTest {
        val handled = mutableListOf<Int>()
        val vm = recordingVm(handled)

        vm.onViewAction(TestViewAction(5))
        scheduler.advanceTimeBy(BaseActionHandleViewModel.SAME_VIEW_ACTION_THROTTLE - 10)
        vm.onViewAction(TestViewAction(5)) // same data, < window -> suppressed
        scheduler.advanceTimeBy(BaseActionHandleViewModel.SAME_VIEW_ACTION_THROTTLE + 10)
        vm.onViewAction(TestViewAction(5)) // same data, > window -> processed again
        scheduler.runCurrent()

        assertEquals(listOf(5, 5), handled)
    }

    // VA-05: The production default time provider is millisecond-scale, not seconds.
    // Every other test injects `{ scheduler.currentTime }` (millis) and so never touches
    // the real default — that blind spot is exactly how an `epochSeconds` default slipped
    // through, silently inflating each window ~1000x (300ms -> 300s). This pins the unit so
    // a regression to a seconds source fails loudly (epoch millis ~1e12 vs seconds ~1e9).
    @Test
    fun default_time_provider_is_in_milliseconds() {
        val referenceMillis = Clock.System.now().toEpochMilliseconds()
        val provided = BaseActionHandleViewModel.DEFAULT_TIME_PROVIDER()

        assertTrue(
            provided in (referenceMillis - 1_000)..(referenceMillis + 1_000),
            "DEFAULT_TIME_PROVIDER must return epoch millis (~$referenceMillis) but was $provided",
        )
    }

    // endregion

    // region UiEvent emission

    @Test
    fun emitEvent_adds_event_to_uiEvents_flow() = runVmTest {
        val received = mutableListOf<UiEvent>()
        val vm = noopVm()

        val collectJob = launch { vm.uiEvents.collect { received += it } }
        scheduler.runCurrent()

        vm.emitEvent(TestPageEvent("x"))
        scheduler.runCurrent()

        assertEquals(1, received.size)
        assertTrue(received.first() is TestPageEvent)

        collectJob.cancel()
    }

    // UE-01: Same ShowMessage is debounced within the ShowMessage window
    @Test
    fun same_ShowMessage_events_are_debounced() = runVmTest {
        val received = mutableListOf<UiEvent>()
        val vm = noopVm()

        val show1 = UiEvent.ShowMessage(TestMessageEventData("A"))
        val show2 = UiEvent.ShowMessage(TestMessageEventData("A"))

        val collectJob = launch { vm.uiEvents.collect { received += it } }
        scheduler.runCurrent()

        vm.emitEvent(show1)
        scheduler.advanceTimeBy(1000) // < ShowMessage window
        vm.emitEvent(show2)
        scheduler.runCurrent()

        assertEquals(listOf<UiEvent>(show1), received)

        collectJob.cancel()
    }

    // UE-02: The same ShowMessage passes again once the ShowMessage window has elapsed
    @Test
    fun ShowMessage_passes_again_after_debounce_window() = runVmTest {
        val received = mutableListOf<UiEvent>()
        val vm = noopVm()

        val show1 = UiEvent.ShowMessage(TestMessageEventData("A"))
        val show2 = UiEvent.ShowMessage(TestMessageEventData("A"))

        val collectJob = launch { vm.uiEvents.collect { received += it } }
        scheduler.runCurrent()

        vm.emitEvent(show1)
        scheduler.runCurrent()
        scheduler.advanceTimeBy(4100) // > debounce window (4000)
        vm.emitEvent(show2)
        scheduler.runCurrent()

        assertEquals<List<UiEvent>>(listOf(show1, show2), received)

        collectJob.cancel()
    }

    // UE-03: Different UiEvent types are not throttled against each other
    @Test
    fun different_UiEvent_types_are_not_throttled_against_each_other() = runVmTest {
        val received = mutableListOf<UiEvent>()
        val vm = noopVm()

        val show = UiEvent.ShowMessage(TestMessageEventData("A"))
        val hide = UiEvent.HideKeyboard

        val collectJob = launch { vm.uiEvents.collect { received += it } }
        scheduler.runCurrent()

        vm.emitEvent(show)
        scheduler.runCurrent()
        scheduler.advanceTimeBy(50)
        vm.emitEvent(hide)
        scheduler.runCurrent()

        assertEquals(listOf(show, hide), received)

        collectJob.cancel()
    }

    // UE-05: A collector started after an event (replay 0) does not receive past events
    @Test
    fun collector_started_after_event_does_not_receive_previous_uiEvents() = runVmTest {
        val vm = noopVm()

        vm.emitEvent(UiEvent.ShowMessage(TestMessageEventData("test")))
        scheduler.runCurrent()

        val received = mutableListOf<UiEvent>()
        val collectJob = launch { vm.uiEvents.collect { received += it } }
        scheduler.runCurrent()

        assertTrue(received.isEmpty())

        collectJob.cancel()
    }

    // MX-03: Cancelling the collector stops delivery without leaks
    @Test
    fun cancelling_uiEvents_collector_stops_delivery() = runVmTest {
        val received = mutableListOf<UiEvent>()
        val vm = noopVm()

        val collectJob = launch { vm.uiEvents.collect { received += it } }
        scheduler.runCurrent()

        vm.emitEvent(UiEvent.ShowMessage(TestMessageEventData("first")))
        scheduler.runCurrent()

        collectJob.cancel()

        vm.emitEvent(UiEvent.ShowMessage(TestMessageEventData("second")))
        scheduler.runCurrent()

        assertEquals(1, received.size)
        assertEquals("first", (received.first() as UiEvent.ShowMessage).data.message)
    }

    // endregion

    // region ViewData state

    // VD-00: Each processed action updates viewData sequentially
    @Test
    fun each_processed_action_updates_viewData_sequentially() = runVmTest {
        val vm = statefulVm()
        val updates = mutableListOf<Int>()

        val collectJob = launch { vm.viewDataState.collect { updates += it.counter } }

        repeat(5) {
            vm.onViewAction(TestViewAction(it))
            scheduler.advanceTimeBy(350) // outside throttle window
        }
        scheduler.runCurrent()
        collectJob.cancel()

        assertEquals(listOf(1, 2, 3, 4, 5), updates.takeLast(5))
    }

    // VD-02: Duplicate inside the throttle window does not change state
    @Test
    fun duplicate_action_inside_throttle_is_ignored_for_viewData() = runVmTest {
        val vm = statefulVm()

        vm.onViewAction(TestViewAction(1))
        scheduler.advanceTimeBy(50) // < window
        vm.onViewAction(TestViewAction(1))
        scheduler.advanceTimeBy(500)

        assertEquals(1, vm.viewDataState.value.counter)
    }

    // VD-04: A concurrent burst of unique actions keeps a consistent final state
    @Test
    fun concurrent_unique_actions_result_in_consistent_counter() = runVmTest {
        val vm = statefulVm()

        coroutineScope {
            repeat(100) { id ->
                launch { vm.onViewAction(TestViewAction(id)) }
            }
        }
        scheduler.advanceUntilIdle()

        assertEquals(100, vm.viewDataState.value.counter)
    }

    // endregion

    // region Error handling

    // ER-01: An exception in handleViewAction does not terminate the ViewModel scope
    @Test
    fun exception_in_handleViewAction_does_not_terminate_ViewModel() = runVmTest {
        var processed = 0
        val vm = object : BaseActionHandleViewModel<TestViewData>(
            dispatchers,
            timeProvider = { scheduler.currentTime },
        ) {
            override val _viewData = MutableStateFlow(TestViewData())
            override suspend fun handleViewAction(action: ViewAction) {
                processed++
                if (processed == 1) throw RuntimeException("boom")
            }
        }

        vm.onViewAction(TestViewAction(1))
        scheduler.advanceTimeBy(100)
        assertEquals(1, processed)

        vm.onViewAction(TestViewAction(2))
        scheduler.advanceTimeBy(100)
        assertEquals(2, processed)
    }

    // ER-02: emitEvent after the scope is closed does not throw or hang
    @Test
    fun emitEvent_after_scope_is_closed_does_not_throw() = runVmTest {
        val vm = noopVm()

        vm.viewModelScope.cancel()
        vm.emitEvent(UiEvent.ShowMessage(TestMessageEventData("test")))
        scheduler.runCurrent()
    }

    // endregion
}