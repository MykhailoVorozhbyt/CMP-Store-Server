package com.feature.home.presentation.view_data

import com.store.core.presentation.ui.ViewAction

sealed interface HomeGraphViewAction : ViewAction {
    data object CheckoutClicked : HomeGraphViewAction
    data object SignOutClicked : HomeGraphViewAction
}
