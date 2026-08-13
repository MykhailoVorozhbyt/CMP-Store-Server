@file:OptIn(ExperimentalForeignApi::class)

package com.store.core.security

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSData
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

//TODO: need test on IOS
class IosSecureStorage : SecureStorage {
    private val service = "com.store.core.security"

    override suspend fun putValue(key: String, value: String) {
        removeByKey(key)
        val attrs = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to service,
            kSecAttrAccount to key,
            kSecValueData to value.encodeToByteArray().toNSData(),
            kSecAttrAccessible to kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        )
        check(SecItemAdd(attrs.toCFDictionary(), null) == errSecSuccess)
    }

    override suspend fun getValue(key: String): String? {
        val query = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to service,
            kSecAttrAccount to key,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        )
        return memScoped {
            val result = alloc<CFTypeRefVar>()
            if (SecItemCopyMatching(
                    query.toCFDictionary(),
                    result.ptr
                ) != errSecSuccess
            ) return null
            (CFBridgingRelease(result.value) as? NSData)?.toByteArray()?.decodeToString()
        }
    }

    override suspend fun removeByKey(key: String) {
        val query = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to service,
            kSecAttrAccount to key
        )
        SecItemDelete(query.toCFDictionary())
    }
}