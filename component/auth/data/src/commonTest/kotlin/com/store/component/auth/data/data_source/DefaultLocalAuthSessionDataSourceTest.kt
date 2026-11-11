package com.store.component.auth.data.data_source

import com.store.core.security.SecureStorage
import com.store.core.security.SessionScopedStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultLocalAuthSessionDataSourceTest {

    private val secureStorage = InMemorySecureStorage()
    private val firstStorage = RecordingSessionScopedStorage()
    private val secondStorage = RecordingSessionScopedStorage()
    private val dataSource = DefaultLocalAuthSessionDataSource(secureStorage, listOf(firstStorage, secondStorage))

    @Test
    fun signOut_clearsEverySessionScopedStorage() = runTest {
        dataSource.setSession(userId = "uid-1", accessToken = "access", refreshToken = "refresh")

        dataSource.signOut()

        assertTrue(firstStorage.cleared)
        assertTrue(secondStorage.cleared)
    }

    @Test
    fun signOut_removesTheSessionFromSecureStorage() = runTest {
        dataSource.setSession(userId = "uid-1", accessToken = "access", refreshToken = "refresh")

        dataSource.signOut()

        assertNull(dataSource.currentUserId())
        assertEquals(emptyMap(), secureStorage.values)
    }

    private class InMemorySecureStorage : SecureStorage {
        val values = mutableMapOf<String, String>()
        override suspend fun putValue(key: String, value: String) {
            values[key] = value
        }
        override suspend fun getValue(key: String): String? = values[key]
        override suspend fun removeByKey(key: String) {
            values.remove(key)
        }
    }

    private class RecordingSessionScopedStorage : SessionScopedStorage {
        var cleared = false
        override suspend fun clear() {
            cleared = true
        }
    }
}
