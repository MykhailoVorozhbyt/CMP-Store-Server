package com.store.core.security

//TODO: add unit test for 3 targets in future
interface SecureStorage {
    suspend fun putValue(key: String, value: String)
    suspend fun getValue(key: String): String?
    suspend fun removeByKey(key: String)
}