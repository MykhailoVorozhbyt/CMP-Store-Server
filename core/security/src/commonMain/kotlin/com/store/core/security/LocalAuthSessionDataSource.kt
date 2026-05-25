package com.store.core.security

interface LocalAuthSessionDataSource {
    suspend fun currentUserId(): String?
    suspend fun currentAccessToken(): String?
    suspend fun currentRefreshToken(): String?
    suspend fun setSession(userId: String, accessToken: String, refreshToken: String)
    suspend fun updateTokens(accessToken: String, refreshToken: String)
    suspend fun signOut()
}
