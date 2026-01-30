package com.store.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.store.core.presentation.theme.color.LocalStoreColors
import com.store.core.presentation.theme.color.StoreColorsPalette
import com.store.core.presentation.theme.color.StoreThemeProvider
import com.store.core.presentation.theme.typography.LocalStoreTypography
import com.store.core.presentation.theme.typography.StoreTypography
import org.koin.compose.koinInject


@Composable
fun BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeProvider = koinInject<StoreThemeProvider>()
    val colors = if (darkTheme) themeProvider.darkPalette else themeProvider.lightPalette
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
