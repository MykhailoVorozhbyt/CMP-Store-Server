package com.store.core.network.utils

import org.cmp.store.network.NetworkError

internal expect fun Throwable.platformNetworkError(): NetworkError?
