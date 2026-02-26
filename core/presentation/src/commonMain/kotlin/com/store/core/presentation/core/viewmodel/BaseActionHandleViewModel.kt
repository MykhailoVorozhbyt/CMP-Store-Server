//@file:Suppress("MemberVisibilityCanBePrivate", "PropertyName", "")
//
//package com.store.core.presentation.core.viewmodel
//
//import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel.Companion.SAME_VIEW_ACTION_THROTTLE
//import kotlinx.coroutines.CoroutineExceptionHandler
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.CoroutineStart
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.channels.BufferOverflow
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.FlowCollector
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.consumeAsFlow
//import kotlinx.coroutines.flow.shareIn
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import kotlin.coroutines.CoroutineContext
//import kotlin.time.Clock
//
///**
// * Base ViewModel for unidirectional UI event and action handling.
// *
// * Implements [ActionHandlerContext] and [UiEventSource] to standardize coroutine execution,
// * view action processing, and throttled UI event delivery.
// *
// * ## Responsibilities
// * - **Centralizes view action dispatch:** Accepts [ViewAction]s via [onViewAction] and processes them
// *   sequentially through [handleViewAction], throttling repeated actions within a configurable window
// *   (see [SAME_VIEW_ACTION_THROTTLE]).
// * - **Publishes UI events:** Exposes a `SharedFlow<UiEvent>` ([uiEvents]) with two levels of throttling:
// *     - **Identity-based throttling:** All identical UI events are debounced as view actions
// *       (see [SAME_UI_EVENT_THROTTLE]). This prevents accidental rapid re-delivery of the same event.
// *     - **Type-based throttling:** Certain UI event types, such as [UiEvent.ShowMessage], are further throttled
// *       so that multiple events of the same type are suppressed within their specific window (see [SAME_UI_SHOW_MESSAGE_THROTTLE]).
// *       For example, repeated [UiEvent.ShowMessage] events are only emitted once per window, regardless of payload.
// * - **Consistent state delivery:** Exposes [viewDataState] as a `StateFlow<VD>` for UI consumption.
// * - **Error handling:** Attaches an [ErrorLoggerI] and [CoroutineExceptionHandler] to all internal launches.
// * - **Launch helpers:** Provides [launch] and [launchSingle] to ensure all coroutines run on the
// *   ViewModel-defined context and error-handling infrastructure.
// *
// *   ⚠️ **IMPORTANT ABOUT YOUR [ViewAction] and [UiEvent]!**
// *
// *   With all this throttling and debouncing, **make sure your [ViewAction] and [UiEvent] implementations
// *   have proper `equals()` and `hashCode()` methods** (or they're data classes/objects).
// *
// * ## Usage
// * - **Action dispatch:**
// *   Call [onViewAction] to send user or system actions; override [handleViewAction] for business logic.
// * - **Event publishing:**
// *   Use [emitEvent] to send `UiEvent`s; [uiEvents] delivers them to the UI, respecting both identity- and type-based throttling.
// * - **Coroutines:**
// *   Prefer [launch] for standard jobs and [launchSingle] for "latest wins" behavior (see [SingleLauncher]).
// *   Both inherit the ViewModel's [mainCoroutineCtx], which includes error logging and dispatcher.
// *
// * ## Implementation Notes
// * - All event and action throttling logic is customizable via companion object constants.
// * - Internal event and action channels are unbounded; throttling/filtering is performed downstream
// *   in flows for predictable UI and business logic response.
// *
// * @param VD Type of the UI state [viewDataState] exposed by this ViewModel.
// * @param givenMainCtx Dispatcher and parent context for all internal coroutines.
// * @param timeProvided Used for tests mostly. Don't bother with it in production code.
// *
// *  @see [ActionHandlerContext]
// *  @see [UiEventSource]
// *  @see [Launcher]
// */
//abstract class BaseActionHandleViewModel<VD>(
//    givenMainCtx: CoroutineContext,
////    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
//    private val timeProvided: () -> Long = { Clock.System.now().epochSeconds }
//) : ViewModel(), ActionHandlerContext, UiEventSource, ActionHandlerScope<VD> {
//
//    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        logError(throwable)
//    }
//
//    final override val mainCoroutineCtx: CoroutineContext = exceptionHandler + givenMainCtx
//    final override val ioContext: CoroutineContext = mainCoroutineCtx + ioDispatcher
//
//    protected abstract val _viewData: MutableStateFlow<VD>
//    val viewDataState: StateFlow<VD> get() = _viewData
//    override val viewData: VD
//        get() { // formatting for easier breakpoint and logs setting
//            return _viewData.value
//        }
//
//    /**
//     * Centralized method for updating ViewData.
//     * Ensure all ViewData changes go through a single point for debugging and monitoring.
//     */
//    override fun updateViewData(update: VD.() -> VD) {
//        logViewDataUpdate(viewData, update)
//        _viewData.update { update(it) }
//    }
//
//    protected val _uiEvents = MutableSharedFlow<UiEvent>(
//        extraBufferCapacity = UI_EVENTS_BUFFER,
//        onBufferOverflow = BufferOverflow.DROP_OLDEST
//    )
//
//    override val uiEvents: SharedFlow<UiEvent> = _uiEvents
//        .distinctUntilChangedDebounced(SAME_UI_EVENT_THROTTLE, timeProvided)
//        .distinctUntilChangedDebouncedByType(
//            SAME_SHOW_MESSAGE_THROTTLE,
//            UiEvent.ShowMessage::class,
//            timeProvided
//        )
//        .distinctUntilChangedDebouncedByType(
//            SAME_SHOW_EXTERNAL_PAGE_THROTTLE,
//            UiEvent.ShowExternalPage::class,
//            timeProvided
//        )
//        .shareIn(viewModelScope, SharingStarted.Lazily)
//
//    override val scope: CoroutineScope get() = viewModelScope
//
//    private val viewActions = Channel<ViewAction>(Channel.UNLIMITED)
//
//    init {
//        launch {
//            viewActions
//                .consumeAsFlow()
//                .distinctUntilChangedDebounced(SAME_VIEW_ACTION_THROTTLE, timeProvided)
//                .collect { action ->
//                    runCatching { handleViewAction(action) }.onFailure { exception ->
//                        ErrorLogger.logError(
//                            "Exception caught while handling ViewAction: $action", exception
//                        )
//                        showError(Const.UNEXPECTED_ERROR)
//                    }
//                }
//        }
//    }
//
//    override fun emitEvent(event: UiEvent) {
//        _uiEvents.emitOnScope(viewModelScope, mainCoroutineCtx, event)
//    }
//
//    fun onViewAction(viewAction: ViewAction) {
//        viewActions.trySend(viewAction)
//    }
//
//    /**
//     * ⚠️Even it is suspend fun, it's one coroutine on main thread. Don't block it!
//     * If you need to do some heavy work, use [launchIo] or [launch] with your context.
//     * P.S. `withContext(IO)` also blocks.
//     */
//    protected abstract suspend fun handleViewAction(action: ViewAction)
//
//    protected fun <T> Flow<T>.collectOnIo(collector: FlowCollector<T>): Job {
//        return collectOnScope(viewModelScope, ioContext, collector)
//    }
//
//    final override fun launch(
//        context: CoroutineContext,
//        start: CoroutineStart,
//        block: suspend CoroutineScope.() -> Unit
//    ): Job {
//        return viewModelScope.launch(mainCoroutineCtx + context, start, block)
//    }
//
//    final override fun launchIo(
//        context: CoroutineContext,
//        start: CoroutineStart,
//        block: suspend CoroutineScope.() -> Unit
//    ): Job {
//        return launch(exceptionHandler + context + ioDispatcher, start, block)
//    }
//
//    private val singleLauncher = SingleJobLaunch.Delegate(viewModelScope)
//    override fun launchSingle(
//        context: CoroutineContext,
//        start: CoroutineStart,
//        jobKey: String,
//        block: suspend CoroutineScope.() -> Unit
//    ): Job {
//        return singleLauncher.launchSingle(mainCoroutineCtx + context, start, jobKey, block)
//    }
//
//    private fun logError(throwable: Throwable) {
////        ErrorLogger.logError(throwable, "Unhandled exception in ViewModel ${this::class.java.name}")
////        showError(Const.UNEXPECTED_ERROR)
//    }
//
//    companion object {
//        const val UI_EVENTS_BUFFER = 32
//        const val SAME_VIEW_ACTION_THROTTLE = 300L
//        const val SAME_UI_EVENT_THROTTLE = 400L
//        const val SAME_SHOW_MESSAGE_THROTTLE = 4000L
//        const val SAME_SHOW_EXTERNAL_PAGE_THROTTLE = 2000L
//    }
//
//}
