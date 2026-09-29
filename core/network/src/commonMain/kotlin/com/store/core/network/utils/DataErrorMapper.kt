package com.store.core.network.utils

import com.store.core.domain.DataError
import org.cmp.store.network.NetworkError

fun NetworkError.toDataError(): DataError = when (this) {
    NetworkError.NO_INTERNET -> DataError.NoInternet
    NetworkError.REQUEST_TIMEOUT -> DataError.Timeout
    NetworkError.UNAUTHORIZED,
    NetworkError.INVALID_REFRESH_TOKEN,
    NetworkError.TOKEN_REUSE_DETECTED -> DataError.Unauthorized
    NetworkError.FORBIDDEN -> DataError.Forbidden
    NetworkError.TOO_MANY_REQUESTS -> DataError.TooManyRequests
    NetworkError.SERVER_ERROR -> DataError.Server
    NetworkError.SERIALIZATION -> DataError.Serialization
    NetworkError.UNKNOWN,
    NetworkError.PAYLOAD_TOO_LARGE,
    NetworkError.USER_ALREADY_EXISTS,
    NetworkError.MISSING_CUSTOMER_ID,
    NetworkError.CUSTOMER_NOT_FOUND,
    NetworkError.PRODUCT_NOT_FOUND,
    NetworkError.INVALID_PRODUCT_CATEGORY,
    NetworkError.NOT_FOUND,
    NetworkError.EMAIL_REQUIRED,
    NetworkError.PASSWORD_REQUIRED,
    NetworkError.PROVIDER_USER_ID_REQUIRED,
    NetworkError.INVALID_CREDENTIALS,
    NetworkError.AUTH_PROVIDER_NOT_SUPPORTED,
    NetworkError.ACCOUNT_HAS_NO_PASSWORD -> DataError.Unknown
}
