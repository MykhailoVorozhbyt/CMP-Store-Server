package com.store.core.security

interface SessionScopedStorage {
    suspend fun clear()
}
