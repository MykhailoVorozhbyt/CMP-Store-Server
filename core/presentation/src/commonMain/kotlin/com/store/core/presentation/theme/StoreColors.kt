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

    // Input text
    val inputBg: Color
    val inputBgDisable: Color
    val inputBorder: Color
    val inputBorderFocus: Color
    val inputBorderError: Color
    val inputBorderSuccess: Color
    val inputTxtPlaceholder: Color
    val inputTxt: Color
    val inputLabelTxt: Color
    val inputHelpTxt: Color
    val inputErrorTxt: Color

    // Brand
    val brand1: Color
    val brand2: Color
    val brand3: Color

    // Text
    val textPrimary: Color
    val textSecondary: Color
    val textBrand: Color

    //Button
    val buttonPrimary: Color
    val buttonPrimaryBorder: Color get() = Color(0xFF000000)

    // Icon
    val iconPrimary: Color
    val iconSecondary: Color

    // border
    val borderIdle: Color get() = Color(0xFFEBEBEB)
    val borderError: Color
    val borderSecondary: Color

    // Surface
    val surface: Color get() = Color(0xFFFFFFFF)
    val surfaceLight: Color
    val surfaceDark: Color get() = Color(0xFFF1F1F1)
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
    val notificationSuccessBg: Color get() = surfaceBrand
    val notificationSuccessTxt: Color get() = Color(0xFFFFFFFF)
    val notificationInfoBg: Color get() = surfaceBrand
    val notificationInfoTxt: Color get() = Color(0xFFFFFFFF)
    val notificationErrorBg: Color get() = surfaceError
    val notificationErrorTxt: Color get() = Color(0xFFFFFFFF)
    val notificationWarningBg: Color get() = Color(0xE9FF9523)
    val notificationWarningTxt: Color get() = Color(0xFFFFFFFF)

    fun getColorByAttr(attr: String): Color =
        when (attr) {
            NOTIFICATION_SUCCESS_COLOR_BG_ATTR -> notificationSuccessBg
            NOTIFICATION_SUCCESS_COLOR_TINT_ATTR -> notificationSuccessTxt
            NOTIFICATION_INFO_COLOR_BG_ATTR -> notificationInfoBg
            NOTIFICATION_INFO_COLOR_TINT_ATTR -> notificationInfoTxt
            NOTIFICATION_ERROR_COLOR_BG_ATTR -> notificationErrorBg
            NOTIFICATION_ERROR_COLOR_TINT_ATTR -> notificationErrorTxt
            NOTIFICATION_WARNING_COLOR_BG_ATTR -> notificationWarningBg
            NOTIFICATION_WARNING_COLOR_TINT_ATTR -> notificationWarningTxt
            else -> throw IllegalArgumentException("The attribute '$attr' is not defined!")
        }

    companion object {
        const val NOTIFICATION_INFO_COLOR_BG_ATTR = "notificationInfoColorBgAttr"
        const val NOTIFICATION_INFO_COLOR_TINT_ATTR = "notificationInfoColorTintAttr"
        const val NOTIFICATION_ERROR_COLOR_BG_ATTR = "notificationErrorColorBgAttr"
        const val NOTIFICATION_ERROR_COLOR_TINT_ATTR = "notificationErrorColorTintAttr"
        const val NOTIFICATION_SUCCESS_COLOR_BG_ATTR = "notificationSuccessColorBgAttr"
        const val NOTIFICATION_SUCCESS_COLOR_TINT_ATTR = "notificationSuccessColorTintAttr"
        const val NOTIFICATION_WARNING_COLOR_BG_ATTR = "notificationWarningColorBgAttr"
        const val NOTIFICATION_WARNING_COLOR_TINT_ATTR = "notificationWarningColorTintAttr"
    }

}

abstract class BaseLightStoreColorsPalette : StoreColorsPalette {
    override val window: Color get() = Color(0xFFFFFFFF)
    override val inputBg: Color get() = Color(0xF6F7F9FF)
    override val inputBgDisable: Color get() = Color(0xF6AAAAAA)
    override val inputBorder: Color get() = Color(0xFFBDBDBD)
    override val inputBorderFocus: Color get() = Color(0xFF000000)
    override val inputBorderError: Color get() = Color(0xFFDA1A32)
    override val inputBorderSuccess: Color get() = Color(0xFF05C489)
    override val inputTxtPlaceholder: Color get() = Color(0xFF949FB6)
    override val inputTxt: Color get() = Color(0xFF000000)
    override val inputLabelTxt: Color get() = Color(0xFF000000)
    override val inputHelpTxt: Color get() = Color(0xFF95A0CD)
    override val inputErrorTxt: Color get() = Color(0xFFDA1A32)
    override val textPrimary: Color get() = Color(0xFF000000)
    override val iconPrimary: Color get() = Color(0xFF000000)
    override val surfaceLight: Color get() = Color(0xFFFAFAFA)
}

abstract class BaseDarkStoreColorsPalette : StoreColorsPalette {
    override val window: Color get() = Color(0xFF000000)
    override val inputBg: Color get() = Color(0xFF121212)
    override val inputBgDisable: Color get() = Color(0xF6242424)
    override val inputBorder: Color get() = Color(0xFF848484)
    override val inputBorderFocus: Color get() = Color(0xFFBFBFBF)
    override val inputBorderError: Color get() = Color(0xFFDA1A32)
    override val inputBorderSuccess: Color get() = Color(0xFF05C489)
    override val inputTxtPlaceholder: Color get() = Color(0xFFA6ACBD)
    override val inputTxt: Color get() = Color(0xFFFFFFFF)
    override val inputLabelTxt: Color get() = Color(0xFFFFFFFF)
    override val inputHelpTxt: Color get() = Color(0xFF95A0CD)
    override val inputErrorTxt: Color get() = Color(0xFFDA1A32)
    override val textPrimary: Color get() = Color(0xFFFFFFFF)
    override val iconPrimary: Color get() = Color(0xFFFFFFFF)
    override val surfaceLight: Color get() = Color(0xFF121212)
}
