package org.cmp.store.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode

suspend fun <T> safeApiCall(
    execute: suspend () -> T
): ApiResult<T, NetworkError> {
    return try {
        ApiResult.Success(execute())
    } catch (e: ClientRequestException) {
        val error = when (e.response.status) {
            HttpStatusCode.Conflict -> NetworkError.USER_ALREADY_EXISTS
            HttpStatusCode.NotFound -> NetworkError.CUSTOMER_NOT_FOUND
            HttpStatusCode.Unauthorized -> NetworkError.UNAUTHORIZED
            HttpStatusCode.RequestTimeout -> NetworkError.REQUEST_TIMEOUT
            HttpStatusCode.TooManyRequests -> NetworkError.TOO_MANY_REQUESTS
            HttpStatusCode.PayloadTooLarge -> NetworkError.PAYLOAD_TOO_LARGE
            else -> NetworkError.UNKNOWN
        }
        ApiResult.Error(error)
    } catch (_: ServerResponseException) {
        ApiResult.Error(NetworkError.SERVER_ERROR)
    } catch (_: HttpRequestTimeoutException) {
        ApiResult.Error(NetworkError.REQUEST_TIMEOUT)
    } catch (_: Exception) {
        ApiResult.Error(NetworkError.UNKNOWN)
    }
}