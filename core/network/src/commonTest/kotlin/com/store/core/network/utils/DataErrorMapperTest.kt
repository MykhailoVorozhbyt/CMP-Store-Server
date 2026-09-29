package com.store.core.network.utils

import com.store.core.domain.DataError
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class DataErrorMapperTest {

    @Test
    fun transport_failures_keep_their_meaning() {
        assertEquals(DataError.NoInternet, NetworkError.NO_INTERNET.toDataError())
        assertEquals(DataError.Timeout, NetworkError.REQUEST_TIMEOUT.toDataError())
        assertEquals(DataError.Server, NetworkError.SERVER_ERROR.toDataError())
        assertEquals(DataError.Serialization, NetworkError.SERIALIZATION.toDataError())
        assertEquals(DataError.TooManyRequests, NetworkError.TOO_MANY_REQUESTS.toDataError())
        assertEquals(DataError.Forbidden, NetworkError.FORBIDDEN.toDataError())
    }

    @Test
    fun a_dead_session_is_unauthorized() {
        assertEquals(DataError.Unauthorized, NetworkError.UNAUTHORIZED.toDataError())
        assertEquals(DataError.Unauthorized, NetworkError.INVALID_REFRESH_TOKEN.toDataError())
        assertEquals(DataError.Unauthorized, NetworkError.TOKEN_REUSE_DETECTED.toDataError())
    }

    @Test
    fun codes_owned_by_a_component_are_unknown_to_the_generic_layer() {
        assertEquals(DataError.Unknown, NetworkError.CUSTOMER_NOT_FOUND.toDataError())
        assertEquals(DataError.Unknown, NetworkError.PRODUCT_NOT_FOUND.toDataError())
        assertEquals(DataError.Unknown, NetworkError.NOT_FOUND.toDataError())
        assertEquals(DataError.Unknown, NetworkError.INVALID_CREDENTIALS.toDataError())
    }
}
