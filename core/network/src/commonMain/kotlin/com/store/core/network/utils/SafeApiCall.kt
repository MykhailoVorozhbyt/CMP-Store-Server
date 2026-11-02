package com.store.core.network.utils

import com.store.core.domain.ApiResult
import com.store.core.utils.Logger
import com.store.core.utils.e
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.SerializationException
import org.cmp.store.network.NetworkError

private const val LOG_TAG = "SafeApiCall"

suspend inline fun <reified T> safeApiCall(
    execute: suspend () -> HttpResponse
): ApiResult<T, NetworkError> {
    return try {
        val result = execute()
        ApiResult.Success(result.body())
    } catch (e: ClientRequestException) {
        e.response.toNetworkError().toApiResult()
    } catch (e: ServerResponseException) {
        e.response.toNetworkError().toApiResult()
    } catch (e: HttpRequestTimeoutException) {
        e.toTransportNetworkError().toApiResult()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        e.toTransportNetworkError().toApiResult()
    }
}

@PublishedApi
internal fun NetworkError.toApiResult(): ApiResult<Nothing, NetworkError> =
    if (this == NetworkError.NO_INTERNET) ApiResult.NoInternet else ApiResult.Error(this)

@PublishedApi
internal fun Throwable.toTransportNetworkError(): NetworkError {
    val error = when (this) {
        is HttpRequestTimeoutException,
        is ConnectTimeoutException,
        is SocketTimeoutException -> NetworkError.REQUEST_TIMEOUT

        is SerializationException,
        is ContentConvertException,
        is NoTransformationFoundException -> NetworkError.SERIALIZATION

        else -> platformNetworkError() ?: NetworkError.UNKNOWN
    }
    Logger.e(tag = LOG_TAG, message = "Request failed as $error", throwable = this)
    return error
}

suspend fun HttpResponse.toNetworkError(): NetworkError {
    val code = runCatching { bodyAsText().trim() }.getOrNull()
    return code
        ?.takeIf { it.isNotBlank() }
        ?.let { text -> runCatching { NetworkError.valueOf(text) }.getOrNull() }
        ?: status.toNetworkError()
}

private fun HttpStatusCode.toNetworkError(): NetworkError = when (this) {
    HttpStatusCode.Conflict -> NetworkError.USER_ALREADY_EXISTS
    HttpStatusCode.NotFound -> NetworkError.NOT_FOUND
    HttpStatusCode.Unauthorized -> NetworkError.UNAUTHORIZED
    HttpStatusCode.Forbidden -> NetworkError.FORBIDDEN
    HttpStatusCode.RequestTimeout -> NetworkError.REQUEST_TIMEOUT
    HttpStatusCode.TooManyRequests -> NetworkError.TOO_MANY_REQUESTS
    HttpStatusCode.PayloadTooLarge -> NetworkError.PAYLOAD_TOO_LARGE
    else -> if (value in 500..599) NetworkError.SERVER_ERROR else NetworkError.UNKNOWN
}
