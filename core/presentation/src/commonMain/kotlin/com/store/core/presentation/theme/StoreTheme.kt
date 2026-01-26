package com.store.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.store.core.presentation.theme.color.BaseDarkPalette
import com.store.core.presentation.theme.color.BaseLightPalette
import com.store.core.presentation.theme.color.LocalStoreColors
import com.store.core.presentation.theme.color.StoreColorsPalette
import com.store.core.presentation.theme.typography.LocalStoreTypography
import com.store.core.presentation.theme.typography.StoreTypography


@Composable
fun BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) BaseLightPalette() else BaseDarkPalette()
    val selectionColors = remember(colors.inputLabelTxt) {
        TextSelectionColors(
            handleColor = colors.inputLabelTxt,
            backgroundColor = Color.Transparent
        )
    }

    CompositionLocalProvider(
        LocalStoreColors provides colors,
        LocalStoreTypography provides StoreTypography.init(),
        LocalTextSelectionColors provides selectionColors,
        content = content
    )
}

object StoreTheme {
    val color: StoreColorsPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreColors.current

    val typography: StoreTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreTypography.current
}
