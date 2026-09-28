package com.store.core.network.utils

import io.ktor.client.engine.darwin.DarwinHttpRequestException
import org.cmp.store.network.NetworkError
import platform.Foundation.NSURLErrorCannotConnectToHost
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDataNotAllowed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorTimedOut

private val NO_INTERNET_CODES = setOf(
    NSURLErrorNotConnectedToInternet,
    NSURLErrorCannotFindHost,
    NSURLErrorCannotConnectToHost,
    NSURLErrorNetworkConnectionLost,
    NSURLErrorDataNotAllowed,
)

internal actual fun Throwable.platformNetworkError(): NetworkError? {
    val error = (this as? DarwinHttpRequestException)?.origin ?: return null
    if (error.domain != NSURLErrorDomain) return null
    return when (error.code) {
        in NO_INTERNET_CODES -> NetworkError.NO_INTERNET
        NSURLErrorTimedOut -> NetworkError.REQUEST_TIMEOUT
        else -> null
    }
}
