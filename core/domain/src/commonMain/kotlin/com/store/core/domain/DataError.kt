package com.store.core.domain

sealed interface DataError {
    data object NoInternet : DataError
    data object Timeout : DataError
    data object Unauthorized : DataError
    data object Forbidden : DataError
    data object TooManyRequests : DataError
    data object Server : DataError
    data object Serialization : DataError
    data object Unknown : DataError
}
