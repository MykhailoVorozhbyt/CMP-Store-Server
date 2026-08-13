package com.feature.authentication.presentation.view_data

import com.store.core.presentation.ui.ViewAction

sealed interface AuthenticationViewAction : ViewAction {
    data object OnSignInClick : AuthenticationViewAction
}