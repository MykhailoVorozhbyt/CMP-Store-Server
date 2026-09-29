package com.store.core.network.utils

import com.store.core.domain.ApiResult
import com.store.core.network.bareTestClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import org.cmp.store.network.NetworkError
import java.net.ConnectException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals

class SafeApiCallJvmTest {

    @Test
    fun unknown_host_is_no_internet() = runTest {
        assertEquals(ApiResult.Error(NetworkError.NO_INTERNET), callFailingWith(UnknownHostException("store.local")))
    }

    @Test
    fun refused_connection_is_no_internet() = runTest {
        assertEquals(ApiResult.Error(NetworkError.NO_INTERNET), callFailingWith(ConnectException("Connection refused")))
    }

    private suspend fun callFailingWith(throwable: Throwable): ApiResult<Map<String, String>, NetworkError> {
        val client = bareTestClient(MockEngine { throw throwable })
        return safeApiCall { client.get("product") }
    }
}
