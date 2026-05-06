package com.store.nutri_sport.theme

import androidx.compose.ui.graphics.Color
import com.store.core.presentation.theme.StoreColorsPalette
import com.store.core.presentation.theme.StoreThemeProvider

class NutriSportStoreThemeProvider() : StoreThemeProvider {
    override val lightPalette: StoreColorsPalette = nutriSportLightColorsPalette
    override val darkPalette: StoreColorsPalette = nutriSportDarkColorsPalette
}

private val nutriSportLightColorsPalette = object : StoreColorsPalette {
    // Window / Inputs
    override val window: Color = Color(0xFFFFFFFF)
    override val inputLabelTxt: Color = Color(0xFF000000)

    // Brand
    override val brandBlack: Color = Color(0xFF000000)
    override val brandWhite: Color = Color(0xFFFFFFFF)
    override val brand1: Color = Color(0xFFEEFF00)
    override val brand2: Color = Color(0xFFF24C00)
    override val brand3: Color = Color(0xFFDD0000)

    // Text
    override val textPrimary: Color = Color(0xFF000000)
    override val textSecondary: Color = Color(0xFFF24C00)
    override val textWhite: Color = Color(0xFFFFFFFF)
    override val textBrand: Color = Color(0xFFE6FF00)

    // Buttons
    override val buttonPrimary: Color = Color(0xFFE6FF00)   // neon yellow
    override val buttonSecondary: Color = Color(0xFFF2F2F2)

    // Icons
    override val iconPrimary: Color = Color(0xFF000000)
    override val iconSecondary: Color = Color(0xFFFF6A00)
    override val iconWhite: Color = Color(0xFFFFFFFF)

    // Borders
    override val borderIdle: Color = Color(0xFFF2F2F2)
    override val borderError: Color = Color(0xFFDD0000)
    override val borderSecondary: Color = Color(0xFFF24C00)

    // Surfaces
    override val surface: Color = Color(0xFFFFFFFF)
    override val surfaceLight: Color = Color(0xFFFAFAFA)
    override val surfaceDark: Color = Color(0xFFEAEAEA)
    override val surfaceError: Color = Color(0xFFE60000)
    override val surfaceBrand: Color = Color(0xFFE6FF00)
    override val surfaceSecondary: Color = Color(0xFFF24C00)

    // Gray scale
    override val gray50: Color = Color(0xFFF7F7F7)
    override val gray100: Color = Color(0xFFF2F2F2)
    override val gray150: Color = Color(0xFFE0E0E0)

    // Categories
    override val category1: Color = Color(0xFFFFC93C)
    override val category2: Color = Color(0xFF3FA9F5)
    override val category3: Color = Color(0xFF22D10F)
    override val category4: Color = Color(0xFF8E63F6)
    override val category5: Color = Color(0xFFFF5A5F)
}

private val nutriSportDarkColorsPalette = object : StoreColorsPalette {
    // Window / Inputs
    override val window: Color = Color(0xFF000000)
    override val inputLabelTxt: Color = Color(0xFF000000)

    // Brand
    override val brandBlack: Color = Color(0xFF000000)
    override val brandWhite: Color = Color(0xFFFFFFFF)
    override val brand1: Color = Color(0xFFEEFF00)
    override val brand2: Color = Color(0xFFF24C00)
    override val brand3: Color = Color(0xFFDD0000)

    // Text
    override val textPrimary: Color = Color(0xFF000000)
    override val textSecondary: Color = Color(0xFFF24C00)
    override val textWhite: Color = Color(0xFFFFFFFF)
    override val textBrand: Color = Color(0xFFE6FF00)

    // Buttons
    override val buttonPrimary: Color = Color(0xFFE6FF00)   // neon yellow
    override val buttonSecondary: Color = Color(0xFFF2F2F2)

    // Icons
    override val iconPrimary: Color = Color(0xFFFFFFFF)
    override val iconSecondary: Color = Color(0xFFF24C00)
    override val iconWhite: Color = Color(0xFFFFFFFF)

    // Borders
    override val borderIdle: Color = Color(0xFFF2F2F2)
    override val borderError: Color = Color(0xFFDD0000)
    override val borderSecondary: Color = Color(0xFFFF6A00)

    // Surfaces
    override val surface: Color = Color(0xFFFFFFFF)
    override val surfaceLight: Color = Color(0xFF121212)
    override val surfaceDark: Color = Color(0xFFEAEAEA)
    override val surfaceError: Color = Color(0xFFE60000)
    override val surfaceBrand: Color = Color(0xFFE6FF00)
    override val surfaceSecondary: Color = Color(0xFFFF6A00)

    // Gray scale
    override val gray50: Color = Color(0xFFF7F7F7)
    override val gray100: Color = Color(0xFFF2F2F2)
    override val gray150: Color = Color(0xFFE0E0E0)

    // Categories
    override val category1: Color = Color(0xFFFFC93C)
    override val category2: Color = Color(0xFF3FA9F5)
    override val category3: Color = Color(0xFF22D10F)
    override val category4: Color = Color(0xFF8E63F6)
    override val category5: Color = Color(0xFFFF5A5F)
}
