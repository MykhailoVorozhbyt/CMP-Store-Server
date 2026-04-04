package com.feature.authentication.presentation.view_data

import com.store.core.presentation.ui.base.UiEvent

sealed interface AuthenticationViewAction : UiEvent {
    object ToMainScreen : AuthenticationViewAction
}
