package com.store.core.network.utils

import org.cmp.store.network.NetworkError
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

internal actual fun Throwable.platformNetworkError(): NetworkError? = when (this) {
    is UnknownHostException,
    is ConnectException,
    is NoRouteToHostException,
    is UnresolvedAddressException -> NetworkError.NO_INTERNET
    is SocketTimeoutException -> NetworkError.REQUEST_TIMEOUT
    else -> null
}
