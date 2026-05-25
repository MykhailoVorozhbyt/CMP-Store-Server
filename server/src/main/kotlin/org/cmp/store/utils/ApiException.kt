package org.cmp.store.utils

import io.ktor.http.HttpStatusCode
import org.cmp.store.network.NetworkError

class ApiException(
    val statusCode: HttpStatusCode,
    val networkError: NetworkError,
) : RuntimeException(networkError.message)