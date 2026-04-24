package com.store.core.presentation.core

import com.store.core.presentation.theme.StoreColorsPalette.Companion.NOTIFICATION_ERROR_COLOR_BG_ATTR
import com.store.core.presentation.theme.StoreColorsPalette.Companion.NOTIFICATION_ERROR_COLOR_TINT_ATTR
import com.store.core.presentation.theme.StoreColorsPalette.Companion.NOTIFICATION_INFO_COLOR_BG_ATTR
import com.store.core.presentation.theme.StoreColorsPalette.Companion.NOTIFICATION_INFO_COLOR_TINT_ATTR
import com.store.core.presentation.theme.StoreColorsPalette.Companion.NOTIFICATION_SUCCESS_COLOR_BG_ATTR
import com.store.core.presentation.theme.StoreColorsPalette.Companion.NOTIFICATION_SUCCESS_COLOR_TINT_ATTR
import com.store.core.resources.Resources
import org.jetbrains.compose.resources.DrawableResource

enum class NotificationType(
    val iconResource: DrawableResource,
    val bgColorAttr: String,
    val tintColorAttr: String
) {
    INFO(
        Resources.Icon.NotificationInfo,
        NOTIFICATION_INFO_COLOR_BG_ATTR,
        NOTIFICATION_INFO_COLOR_TINT_ATTR
    ),
    ERROR(
        Resources.Icon.NotificationError,
        NOTIFICATION_ERROR_COLOR_BG_ATTR,
        NOTIFICATION_ERROR_COLOR_TINT_ATTR
    ),
    SUCCESS(
        Resources.Icon.NotificationSuccess,
        NOTIFICATION_SUCCESS_COLOR_BG_ATTR,
        NOTIFICATION_SUCCESS_COLOR_TINT_ATTR
    )
}