package com.feature.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {
    private val _viewData = MutableStateFlow(AuthenticationViewData())
    val viewData: StateFlow<AuthenticationViewData>
        get() = MutableStateFlow(AuthenticationViewData())

    init {
        viewModelScope.launch {
            _viewData.update { it.copy(isLoading = true) }
            delay(1000L)
            _viewData.update { it.copy(isLoading = false) }
        }
    }

}