@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.store.core.security

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy

/**
 * Copies the Kotlin bytes into an immutable [NSData]. The bytes are pinned only for the
 * duration of the copy, so the returned object owns its own buffer.
 */
internal fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.convert())
    }
}

/**
 * Copies an [NSData] payload back into a Kotlin [ByteArray].
 */
internal fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).apply {
        usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    }
}

/**
 * Builds a `CFDictionary` from a map whose keys are already `CFStringRef` constants (e.g. the
 * `kSec*` Keychain attribute keys) and whose values are either CoreFoundation constants or Kotlin
 * objects that need toll-free bridging.
 *
 * Kotlin [String] / [NSData] values are bridged with [CFBridgingRetain] (a +1 retain). Because the
 * dictionary is created with `kCFTypeDictionaryValueCallBacks` it retains each value again, so we
 * release our bridging retain afterward to avoid leaking. CF constants are passed through untouched.
 */
internal fun Map<CFStringRef?, *>.toCFDictionary(): CFDictionaryRef {
    val bridged = mutableListOf<CFTypeRef?>()
    val cfValues = values.map { value ->
        when (value) {
            is String, is NSData -> CFBridgingRetain(value).also { bridged += it }
            else -> value as? CFTypeRef
        }
    }
    return memScoped {
        val cfKeys = allocArrayOf(keys.toList())
        val cfVals = allocArrayOf(cfValues)
        val dictionary = CFDictionaryCreate(
            allocator = null,
            keys = cfKeys.reinterpret(),
            values = cfVals.reinterpret(),
            numValues = size.convert(),
            keyCallBacks = kCFTypeDictionaryKeyCallBacks.ptr,
            valueCallBacks = kCFTypeDictionaryValueCallBacks.ptr,
        )
        // The dictionary copied/retained every value, so drop our bridging retains now.
        // Done before the null check so the failure path can never leak them.
        bridged.forEach { ref -> ref?.let(::CFRelease) }
        checkNotNull(dictionary) {
            "CFDictionaryCreate returned null while building $size Keychain attributes"
        }
    }
}
