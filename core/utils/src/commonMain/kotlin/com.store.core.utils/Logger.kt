package com.store.core.utils

// commonMain/utils/Logger.kt
enum class LogLevel { DEBUG, INFO, WARNING, ERROR }

expect object Logger {
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)
}

private const val TAG = "StoreLog"

// Зручні extension-функції
fun Logger.d(message: String) = log(LogLevel.DEBUG, TAG, message)
fun Logger.d(tag: String = TAG, message: String) = log(LogLevel.DEBUG, tag, message)
fun Logger.i(message: String) = log(LogLevel.INFO, TAG, message)
fun Logger.i(tag: String = TAG, message: String) = log(LogLevel.INFO, tag, message)
fun Logger.w(message: String) = log(LogLevel.WARNING, TAG, message)
fun Logger.w(tag: String = TAG, message: String) = log(LogLevel.WARNING, tag, message)
fun Logger.e(message: String, throwable: Throwable? = null) = log(LogLevel.ERROR, TAG, message, throwable)
fun Logger.e(tag: String = TAG, message: String, throwable: Throwable? = null) =
    log(LogLevel.ERROR, tag, message, throwable)