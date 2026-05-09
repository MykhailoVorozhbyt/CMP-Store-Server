package org.cmp.store.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.CancellationException

suspend fun <T> safeApiCall(
    execute: suspend () -> T
): ApiResult<T, NetworkError> {
    return try {
        val result = execute()
        if (result is HttpResponse && result.status.value !in 200..299) {
            ApiResult.Error(result.status.toNetworkError())
        } else {
            ApiResult.Success(result)
        }
    } catch (e: ClientRequestException) {
        ApiResult.Error(e.response.status.toNetworkError())
    } catch (_: ServerResponseException) {
        ApiResult.Error(NetworkError.SERVER_ERROR)
    } catch (_: HttpRequestTimeoutException) {
        ApiResult.Error(NetworkError.REQUEST_TIMEOUT)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        print(e.message)
        ApiResult.Error(NetworkError.UNKNOWN)
    }
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
