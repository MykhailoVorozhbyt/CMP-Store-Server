package com.store.feature.profile.view_data

import com.store.core.presentation.ui.ViewAction

sealed interface ProfileViewAction : ViewAction {
    data object UpdateClicked : ProfileViewAction
    data object CountryPickerClicked : ProfileViewAction
    data class CountryHighlighted(val country: Country) : ProfileViewAction
    data object CountryConfirmed : ProfileViewAction
    data object CountryPickerDismissed : ProfileViewAction
}
