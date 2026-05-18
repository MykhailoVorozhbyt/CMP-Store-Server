package com.store.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.store.core.presentation.utils.StoreThemeProviderPreviewApi
import org.koin.compose.koinInject

object StoreTheme {
    val color: StoreColorsPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreColors.current

    val typography: StoreTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreTypography.current

    val dimens: StoreDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreDimens.current

    val windowTypography: WindowType
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreWindowType.current

    val strings: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalStoreStrings.current
}

/**
 * Do not use for preview!
 * */
@Composable
fun BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeProvider = koinInject<StoreThemeProvider>()
    val stringsProvider = koinInject<AppStrings>()
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
        LocalStoreWindowType provides rememberWindowType(),
        LocalStoreDimens provides rememberDimens(),
        LocalTextSelectionColors provides selectionColors,
        LocalStoreStrings provides stringsProvider,
        content = content
    )
}

/**
 * Use only for preview!
 * */
@Composable
fun PreviewTheme(
    content: @Composable () -> Unit
) {
    val theme = StoreThemeProviderPreviewApi()
    val colors = if (isSystemInDarkTheme()) theme.darkPalette else theme.lightPalette
    val stringsProvider = object : AppStrings {}
    CompositionLocalProvider(
        LocalStoreColors provides colors,
        LocalStoreTypography provides StoreTypography.init(),
        LocalStoreWindowType provides rememberWindowType(),
        LocalStoreDimens provides rememberDimens(),
        LocalStoreStrings provides stringsProvider,
        content = content
    )
}