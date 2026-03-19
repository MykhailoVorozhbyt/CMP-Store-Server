package com.store.core.presentation.ui.base

import com.store.core.presentation.core.NotificationType

interface MessageEventData {
    val message: String
    val type: NotificationType

    companion object {
        operator fun invoke(message: String, type: NotificationType) = Base(message, type)
        fun error(message: String) = Base(message, NotificationType.ERROR)
        fun info(message: String) = Base(message, NotificationType.INFO)
        fun success(message: String) = Base(message, NotificationType.SUCCESS)
    }

    data class Base(
        override val message: String,
        override val type: NotificationType = NotificationType.ERROR
    ) : MessageEventData
}