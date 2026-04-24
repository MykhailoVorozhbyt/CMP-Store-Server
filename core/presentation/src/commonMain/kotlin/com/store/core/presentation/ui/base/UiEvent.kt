package com.store.core.presentation.ui.base

interface UiEvent {
    interface ShowMessageExtendable : UiEvent {
        val data: MessageEventData
    }

    data class ShowMessage(override val data: MessageEventData) : ShowMessageExtendable

    data object HideKeyboard : UiEvent
    data object ShowKeyboard : UiEvent
    data object ClearFocus : UiEvent

}