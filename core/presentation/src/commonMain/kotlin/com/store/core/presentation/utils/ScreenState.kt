package com.store.core.presentation.utils

import androidx.compose.runtime.Stable

@Stable
sealed interface ScreenState {
    data object Error : ScreenState
    data object Loading : ScreenState
    data object NoInternetConnection : ScreenState
    data object Success : ScreenState
}

fun ScreenState.isError() = this is ScreenState.Error
fun ScreenState.isLoading() = this is ScreenState.Loading
fun ScreenState.isNoInternetConnection() = this is ScreenState.NoInternetConnection
fun ScreenState.isSuccess() = this is ScreenState.Success
