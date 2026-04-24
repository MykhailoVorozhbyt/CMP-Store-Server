package com.store.core.utils

actual object Logger {
    actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val prefix = when (level) {
            LogLevel.DEBUG   -> "🔵 DEBUG"
            LogLevel.INFO    -> "🟢 INFO"
            LogLevel.WARNING -> "🟡 WARN"
            LogLevel.ERROR   -> "🔴 ERROR"
        }
        val log = "[$prefix][$tag] $message"
        if (throwable != null) {
            println("$log\n${throwable.message}")
        } else {
            println(log)
        }
    }
}