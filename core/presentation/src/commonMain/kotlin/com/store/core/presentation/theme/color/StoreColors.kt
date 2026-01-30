package com.store.core.presentation.theme.color

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


val LocalStoreColors = staticCompositionLocalOf<StoreColorsPalette> {
    error("No colors provided")
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
}

class ColorsPalette : StoreColorsPalette {
    // Window / inputs
    override val window: Color = Color(0xFF1296FF)
    override val inputLabelTxt: Color = Color(0xFF000000)

    // Brand
    override val brandBlack: Color = Color(0xFF000000)
    override val brandWhite: Color = Color(0xFFFFFFFF)
    override val brand1: Color = Color(0xFF1296FF)
    override val brand2: Color = Color(0xFF35FFB8)
    override val brand3: Color = Color(0xFFBD5858)

    // Text
    override val textPrimary: Color = Color(0xFF000000)
    override val textSecondary: Color = Color(0xFF0095FF)
    override val textWhite: Color = Color(0xFFFFFFFF)
    override val textBrand: Color = Color(0xFF1296FF)

    // Button
    override val buttonPrimary: Color = Color(0xFF35FFB8)
    override val buttonSecondary: Color = Color(0xFFF2F2F2)

    // Icon
    override val iconPrimary: Color = Color(0xFF000000)
    override val iconSecondary: Color = Color(0xFF1296FF)
    override val iconWhite: Color = Color(0xFFFFFFFF)

    // Border
    override val borderIdle: Color = Color(0xFFE6E6E6)
    override val borderError: Color = Color(0xFFC85C5C)
    override val borderSecondary: Color = Color(0xFF1296FF)

    // Surface
    override val surface: Color = Color(0xFFFFFFFF)
    override val surfaceLight: Color = Color(0xFFFAFAFA)
    override val surfaceDark: Color = Color(0xFFF1F1F1)
    override val surfaceError: Color = Color(0xFFBD5858)
    override val surfaceBrand: Color = Color(0xFF35FFB8)
    override val surfaceSecondary: Color = Color(0xFF0095FF)

    // Gray
    override val gray50: Color = Color(0xFFFFFFFF)
    override val gray100: Color = Color(0xFFFAFAFA)
    override val gray150: Color = Color(0xFFEBEBEB)

    // Category
    override val category1: Color = Color(0xFFFFC83D)
    override val category2: Color = Color(0xFF38B3FF)
    override val category3: Color = Color(0xFF2ED600)
    override val category4: Color = Color(0xFF8B5CFF)
    override val category5: Color = Color(0xFFFF6262)
}