package com.feature.home.presentation.view_data

import com.store.core.presentation.ui.base.UiEvent
import org.cmp.store.navigation.Screen

sealed interface HomeGraphUiEvent : UiEvent {
    data class Navigate(val screen: Screen) : HomeGraphUiEvent
}