package com.store.core.presentation.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.store.core.presentation.core.NotificationType
import com.store.core.presentation.ui.components.StoreSnackbarHostState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow

interface UiEventEmitter {
    fun emitEvent(event: UiEvent)

    fun showMessage(message: String, type: NotificationType) {
        emitEvent(UiEvent.ShowMessage(MessageEventData.Base(message, type)))
    }

    fun showSuccess(message: String) = showMessage(message, NotificationType.SUCCESS)
    fun showError(message: String) = showMessage(message, NotificationType.ERROR)
    fun showInfo(message: String) = showMessage(message, NotificationType.INFO)
}

interface UiEventSource {
    val uiEvents: SharedFlow<UiEvent>
}

@Suppress("ComposableFunctionName", "ComposableNaming")
@Composable
private fun UiEventSource.collectEvents(collector: FlowCollector<UiEvent>) {
    LaunchedEffect(this) {
        uiEvents.collect(collector)
    }
}

@Suppress("ComposableFunctionName", "ComposableNaming")
@Composable
fun UiEventSource.collectEventsWithDefaultProcessing(
    snackbarHostState: StoreSnackbarHostState? = null,
    processCustom: suspend (event: UiEvent, defaultProcess: suspend (event: UiEvent) -> Unit) -> Unit = { event, defaultProcess ->
        defaultProcess(event)
    },
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    collectEvents { event ->
        processCustom(event) { event ->
            when (event) {
                is UiEvent.ShowMessage -> snackbarHostState?.show(event.data)
                is UiEvent.ClearFocus -> focusManager.clearFocus()
                is UiEvent.ShowKeyboard -> keyboardController?.show()
                is UiEvent.HideKeyboard -> keyboardController?.hide()
            }
        }
    }
}