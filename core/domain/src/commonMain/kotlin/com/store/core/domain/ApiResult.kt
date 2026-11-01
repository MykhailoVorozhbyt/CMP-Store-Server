package com.store.core.domain

sealed interface ApiResult<out D, out E : Any> {
    data class Success<out D>(val data: D) : ApiResult<D, Nothing>
    data class Error<out E : Any>(val error: E) : ApiResult<Nothing, E>
    data object NoInternet : ApiResult<Nothing, Nothing>
}

typealias EmptyResult<E> = ApiResult<Unit, E>

@Deprecated("Need remove")
inline fun <T, E : Any> ApiResult<T, E>.onSuccess(action: (T) -> Unit): ApiResult<T, E> {
    if (this is ApiResult.Success) action(data)
    return this
}

@Deprecated("Need remove")
inline fun <T, E : Any> ApiResult<T, E>.onError(action: (E) -> Unit): ApiResult<T, E> {
    if (this is ApiResult.Error) action(error)
    return this
}

@Deprecated("Need remove")
inline fun <T, E : Any> ApiResult<T, E>.onNoInternet(action: () -> Unit): ApiResult<T, E> {
    if (this is ApiResult.NoInternet) action()
    return this
}

inline fun <T, R, E : Any> ApiResult<T, E>.mapSuccess(transform: (T) -> R): ApiResult<R, E> =
    when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> ApiResult.Error(error)
        ApiResult.NoInternet -> ApiResult.NoInternet
    }

inline fun <T, E : Any, F : Any> ApiResult<T, E>.mapError(transform: (E) -> F): ApiResult<T, F> =
    when (this) {
        is ApiResult.Success -> ApiResult.Success(data)
        ApiResult.NoInternet -> ApiResult.NoInternet
        is ApiResult.Error -> ApiResult.Error(transform(error))
    }

inline fun <T, R, E : Any> ApiResult<T, E>.mapToDomainResult(
    transform: (T) -> R,
): ApiResult<R, E> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(data))
    is ApiResult.Error -> ApiResult.Error(error)
    ApiResult.NoInternet -> ApiResult.NoInternet
}
