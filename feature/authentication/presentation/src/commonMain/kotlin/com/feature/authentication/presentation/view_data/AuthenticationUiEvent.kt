package com.feature.authentication.presentation.view_data

import com.store.core.presentation.ui.base.UiEvent
import com.store.core.presentation.utils.UiText

sealed interface AuthenticationUiEvent : UiEvent {
    data class ToMain(val welcomeMessage: UiText) : AuthenticationUiEvent
}
