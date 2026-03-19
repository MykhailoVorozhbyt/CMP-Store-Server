package com.store.core.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalStoreColors = staticCompositionLocalOf<StoreColorsPalette> {
    error("LocalStoreColors no provided")
}

interface StoreThemeProvider {
    val lightPalette: StoreColorsPalette
    val darkPalette: StoreColorsPalette
}

interface StoreColorsPalette {
    val window: Color
    val inputLabelTxt: Color

    // Brand
    val brandBlack: Color
    val brandWhite: Color
    val brand1: Color
    val brand2: Color
    val brand3: Color

    // Text
    val textPrimary: Color
    val textSecondary: Color
    val textWhite: Color
    val textBrand: Color

    //Button
    val buttonPrimary: Color
    val buttonSecondary: Color

    // Icon
    val iconPrimary: Color
    val iconSecondary: Color
    val iconWhite: Color

    // border
    val borderIdle: Color
    val borderError: Color
    val borderSecondary: Color

    // Surface
    val surface: Color
    val surfaceLight: Color
    val surfaceDark: Color
    val surfaceError: Color
    val surfaceBrand: Color
    val surfaceSecondary: Color

    // Gray
    val gray50: Color
    val gray100: Color
    val gray150: Color

    // Category
    val category1: Color
    val category2: Color
    val category3: Color
    val category4: Color
    val category5: Color

    // Notification
    val notificationSuccessBg: Color get() = Color(0xFFFFC83D)
    val notificationSuccessTxt: Color get() = Color(0xFFFFFFFF)
    val notificationInfoBg: Color get() = Color(0xFFFFC83D)
    val notificationInfoTxt: Color get() = Color(0xFFFFFFFF)
    val notificationErrorBg: Color get() = Color(0xFFFFC83D)
    val notificationErrorTxt: Color get() = Color(0xFFFFFFFF)

    fun getColorByAttr(attr: String): Color =
        when (attr) {
            NOTIFICATION_SUCCESS_COLOR_BG_ATTR -> notificationSuccessBg
            NOTIFICATION_SUCCESS_COLOR_TINT_ATTR -> notificationSuccessTxt
            NOTIFICATION_INFO_COLOR_BG_ATTR -> notificationInfoBg
            NOTIFICATION_INFO_COLOR_TINT_ATTR -> notificationInfoTxt
            NOTIFICATION_ERROR_COLOR_BG_ATTR -> notificationErrorBg
            NOTIFICATION_ERROR_COLOR_TINT_ATTR -> notificationErrorTxt
            else -> throw IllegalArgumentException("The attribute '$attr' is not defined!")
        }

    companion object {
        const val NOTIFICATION_INFO_COLOR_BG_ATTR = "notificationInfoColorBgAttr"
        const val NOTIFICATION_INFO_COLOR_TINT_ATTR = "notificationInfoColorTintAttr"
        const val NOTIFICATION_ERROR_COLOR_BG_ATTR = "notificationErrorColorBgAttr"
        const val NOTIFICATION_ERROR_COLOR_TINT_ATTR = "notificationErrorColorTintAttr"
        const val NOTIFICATION_SUCCESS_COLOR_BG_ATTR = "notificationSuccessColorBgAttr"
        const val NOTIFICATION_SUCCESS_COLOR_TINT_ATTR = "notificationSuccessColorTintAttr"
    }

}