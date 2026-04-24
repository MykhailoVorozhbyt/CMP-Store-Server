package org.cmp.store.network

sealed interface ApiResult<out D, out E : Any> {
    data class Success<out D>(val data: D) : ApiResult<D, Nothing>
    data class Error<out E : Any>(val error: E) : ApiResult<Nothing, E>
}

typealias EmptyResult<E> = ApiResult<Unit, E>

inline fun <T, E : Any> ApiResult<T, E>.onSuccess(action: (T) -> Unit): ApiResult<T, E> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T, E : Any> ApiResult<T, E>.onError(action: (E) -> Unit): ApiResult<T, E> {
    if (this is ApiResult.Error) action(error)
    return this
}