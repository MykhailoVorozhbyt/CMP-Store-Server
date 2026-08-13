package com.store.core.presentation.utils

import androidx.compose.ui.graphics.Color
import com.store.core.presentation.theme.BaseDarkStoreColorsPalette
import com.store.core.presentation.theme.BaseLightStoreColorsPalette
import com.store.core.presentation.theme.StoreColorsPalette
import com.store.core.presentation.theme.StoreThemeProvider

class StoreThemeProviderPreviewApi : StoreThemeProvider {
    override val lightPalette: StoreColorsPalette = lightColorsPalette
    override val darkPalette: StoreColorsPalette = darkColorsPalette
}

private val lightColorsPalette = object : BaseLightStoreColorsPalette() {
    // Brand
    override val brand1: Color = Color(0xFF1296FF)
    override val brand2: Color = Color(0xFF35FFB8)
    override val brand3: Color = Color(0xFFBD5858)

    // Text
    override val textSecondary: Color = Color(0xFF0095FF)
    override val textBrand: Color = Color(0xFF1296FF)

    // Button
    override val buttonPrimary: Color = Color(0xFF35FFB8)

    // Icon
    override val iconSecondary: Color = Color(0xFF1296FF)

    // Border
    override val borderError: Color = Color(0xFFC85C5C)
    override val borderSecondary: Color = Color(0xFF1296FF)

    // Surface
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


private val darkColorsPalette = object : BaseDarkStoreColorsPalette() {
    // Brand
    override val brand1: Color = Color(0xFF1296FF)
    override val brand2: Color = Color(0xFF35FFB8)
    override val brand3: Color = Color(0xFFBD5858)

    // Text
    override val textSecondary: Color = Color(0xFF0095FF)
    override val textBrand: Color = Color(0xFF1296FF)

    // Button
    override val buttonPrimary: Color = Color(0xFF35FFB8)

    // Icon
    override val iconSecondary: Color = Color(0xFF1296FF)

    // Border
    override val borderError: Color = Color(0xFFC85C5C)
    override val borderSecondary: Color = Color(0xFF1296FF)

    // Surface
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
