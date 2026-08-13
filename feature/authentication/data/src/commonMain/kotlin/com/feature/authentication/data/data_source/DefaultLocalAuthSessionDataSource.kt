package com.feature.authentication.data.data_source

import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.security.SecureStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

open class DefaultLocalAuthSessionDataSource(
    private val secureStorage: SecureStorage,
) : LocalAuthSessionDataSource {

    private val mutex = Mutex()
    private var cachedUserId: String? = null
    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null
    private var hydrated = false

    private suspend fun hydrate() {
        if (hydrated) return
        cachedUserId = secureStorage.getValue(KEY_USER_ID)
        cachedAccessToken = secureStorage.getValue(KEY_ACCESS_TOKEN)
        cachedRefreshToken = secureStorage.getValue(KEY_REFRESH_TOKEN)
        hydrated = true
    }

    override suspend fun currentUserId(): String? {
        mutex.withLock {
            hydrate()
            return cachedUserId
        }
    }

    override suspend fun currentAccessToken(): String? {
        mutex.withLock {
            hydrate()
            return cachedAccessToken
        }
    }

    override suspend fun currentRefreshToken(): String? {
        mutex.withLock {
            hydrate()
            return cachedRefreshToken
        }
    }

    override suspend fun setSession(userId: String, accessToken: String, refreshToken: String) {
        mutex.withLock {
            cachedUserId = userId
            hydrated = true
            secureStorage.putValue(KEY_USER_ID, userId)
            writeTokens(accessToken, refreshToken)
        }
    }

    override suspend fun updateTokens(accessToken: String, refreshToken: String) {
        mutex.withLock {
            hydrate()
            writeTokens(accessToken, refreshToken)
        }
    }

    override suspend fun signOut() {
        mutex.withLock {
            cachedUserId = null
            cachedAccessToken = null
            cachedRefreshToken = null
            hydrated = true
            secureStorage.removeByKey(KEY_USER_ID)
            secureStorage.removeByKey(KEY_ACCESS_TOKEN)
            secureStorage.removeByKey(KEY_REFRESH_TOKEN)
        }
    }

    /** Caller must hold [mutex]. */
    private suspend fun writeTokens(accessToken: String, refreshToken: String) {
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken
        hydrated = true
        secureStorage.putValue(KEY_ACCESS_TOKEN, accessToken)
        secureStorage.putValue(KEY_REFRESH_TOKEN, refreshToken)
    }

    companion object {
        private const val KEY_USER_ID = "auth_user_id"
        private const val KEY_ACCESS_TOKEN = "auth_access_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
    }
}
