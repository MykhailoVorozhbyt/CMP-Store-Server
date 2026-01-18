package com.store.core.presentation.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


val LocalStoreColors = staticCompositionLocalOf<StoreColorsPalette> {
    error("No colors provided")
}

val baseLightPalette = object : StoreColorsPalette {
    override val window = Color(255, 255, 255)
    override val inputLabelTxt = Color(98, 98, 98, 255)
}

val baseDarkPalette = object : StoreColorsPalette {
    override val window = Color(255, 255, 255)
    override val inputLabelTxt = Color(0, 0, 0)
}
