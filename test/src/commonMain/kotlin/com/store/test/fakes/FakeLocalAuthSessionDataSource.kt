package com.store.test.fakes

import com.store.core.security.LocalAuthSessionDataSource

class FakeLocalAuthSessionDataSource : LocalAuthSessionDataSource {
    var userId: String? = null
    var accessToken: String? = null
    var refreshToken: String? = null
    var signOutCalled = false
    var signOutThrowable: Throwable? = null

    override suspend fun currentUserId(): String? = userId
    override suspend fun currentAccessToken(): String? = accessToken
    override suspend fun currentRefreshToken(): String? = refreshToken

    override suspend fun setSession(userId: String, accessToken: String, refreshToken: String) {
        this.userId = userId
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override suspend fun updateTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override suspend fun signOut() {
        signOutThrowable?.let { throw it }
        signOutCalled = true
        userId = null
        accessToken = null
        refreshToken = null
    }
}