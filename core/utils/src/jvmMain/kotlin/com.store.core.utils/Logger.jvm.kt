package com.store.core.utils

import java.util.logging.Level
import java.util.logging.Logger as JLogger

actual object Logger {
    private val jLogger = JLogger.getLogger("KMPApp")

    actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val jLevel = when (level) {
            LogLevel.DEBUG -> Level.FINE
            LogLevel.INFO -> Level.INFO
            LogLevel.WARNING -> Level.WARNING
            LogLevel.ERROR -> Level.SEVERE
        }
        jLogger.log(jLevel, "[$tag] $message", throwable)
    }
}