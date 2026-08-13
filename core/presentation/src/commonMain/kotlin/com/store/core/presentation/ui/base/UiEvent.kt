package com.store.core.presentation.ui.base

import com.store.core.presentation.navigation.Screen

interface UiEvent {
    interface ShowMessageExtendable : UiEvent {
        val data: MessageEventData
    }

    data class ShowMessage(override val data: MessageEventData) : ShowMessageExtendable

    data object HideKeyboard : UiEvent
    data object ShowKeyboard : UiEvent
    data object ClearFocus : UiEvent

    data class Navigate(val screen: Screen) : UiEvent
    data class NavigateInclusive(val screen: Screen) : UiEvent

}