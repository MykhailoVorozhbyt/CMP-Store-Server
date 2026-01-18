package com.store.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.store.core.presentation.typography.LocalStoreTypography
import com.store.core.presentation.typography.StoreTypography
import com.store.core.presentation.ui.LocalStoreColors
import com.store.core.presentation.ui.StoreColorsPalette
import com.store.core.presentation.ui.baseDarkPalette
import com.store.core.presentation.ui.baseLightPalette

@Composable
fun BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) baseDarkPalette else baseLightPalette
    val selectionColors = remember(colors.inputLabelTxt) {
        TextSelectionColors(
            handleColor = colors.inputLabelTxt,
            backgroundColor = Color.Transparent
        )
    }

    CompositionLocalProvider(
        LocalStoreColors provides colors,
        LocalStoreTypography provides StoreTypography(),
        LocalTextSelectionColors provides selectionColors,
        content = content
    )
}


typealias StoreTheme = BaseTheme

object BaseTheme {
    val colors: StoreColorsPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreColors.current

    val typography: StoreTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreTypography.current
}
