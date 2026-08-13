package com.store.core.network.utils

import com.store.core.domain.ApiResult
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.CancellationException
import org.cmp.store.network.NetworkError

suspend inline fun <reified T> safeApiCall(
    execute: suspend () -> HttpResponse
): ApiResult<T, NetworkError> {
    return try {
        val result = execute()
        ApiResult.Success(result.body())
    } catch (e: ClientRequestException) {
        ApiResult.Error(e.response.toNetworkError())
    } catch (e: ServerResponseException) {
        ApiResult.Error(e.response.toNetworkError())
    } catch (_: HttpRequestTimeoutException) {
        ApiResult.Error(NetworkError.REQUEST_TIMEOUT)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        print(e.message)
        ApiResult.Error(NetworkError.UNKNOWN)
    }
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
    HttpStatusCode.NotFound -> NetworkError.CUSTOMER_NOT_FOUND
    HttpStatusCode.Unauthorized -> NetworkError.UNAUTHORIZED
    HttpStatusCode.RequestTimeout -> NetworkError.REQUEST_TIMEOUT
    HttpStatusCode.TooManyRequests -> NetworkError.TOO_MANY_REQUESTS
    HttpStatusCode.PayloadTooLarge -> NetworkError.PAYLOAD_TOO_LARGE
    else -> if (value in 500..599) NetworkError.SERVER_ERROR else NetworkError.UNKNOWN
}
