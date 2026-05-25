package com.feature.authentication.presentation.view_data

import com.store.core.presentation.ui.base.UiEvent

sealed interface AuthenticationUiEvent : UiEvent {
    data class ToMainScreen(val welcomeMessage: String) : AuthenticationUiEvent
}
