package com.store.core.presentation.theme.color

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


val LocalStoreColors = staticCompositionLocalOf<StoreColorsPalette> {
    error("No colors provided")
}

open class BaseLightPalette() : StoreColorsPalette {
    override val window = Color(255, 255, 255)
    override val inputLabelTxt = Color(98, 98, 98, 255)
}

open class BaseDarkPalette() : StoreColorsPalette {
    override val window = Color(255, 255, 255)
    override val inputLabelTxt = Color(0, 0, 0)
}
