package com.store.core.presentation.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isIdle(): Boolean = this is Idle
    fun isLoading(): Boolean = this is Loading
    fun isError(): Boolean = this is Error
    fun isSuccess(): Boolean = this is Success

    fun successData() = (this as Success).data
    fun successDataOrNull() = if (this.isSuccess()) this.successData() else null
    fun errorMessage(): String = (this as Error).message
}