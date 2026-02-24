package com.store.core.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.WindowType.Compact
import com.store.core.presentation.theme.WindowType.Expanded
import com.store.core.presentation.theme.WindowType.ExtraLarge
import com.store.core.presentation.theme.WindowType.Large
import com.store.core.presentation.theme.WindowType.Medium

val LocalStoreWindowType = staticCompositionLocalOf<WindowType> {
    error("LocalStoreWindowType no provided")
}

@Composable
fun rememberWindowType(): WindowType {
    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize

    val widthDp = with(density) {
        containerSize.width.toDp()
    }

    return when {
        widthDp < 600.dp -> Compact
        widthDp < 840.dp -> Medium
        widthDp < 1200.dp -> Expanded
        widthDp < 1600.dp -> Large
        else -> ExtraLarge
    }
}

/**
 * Represents width-based window size classes.
 * Breakpoints:
 * @param Compact:    width < 600dp
 *      ~99% of phones in portrait
 * @param Medium:     600dp ≤ width < 840dp
 *      Most tablets in portrait
 *      Large foldables in portrait
 * @param Expanded:   840dp ≤ width < 1200dp
 *      Tablets in landscape
 *      Large unfolded foldables in landscape
 * @param Large:      1200dp ≤ width < 1600dp
 *      Large tablet displays
 * @param ExtraLarge: width ≥ 1600dp
 *      Desktop displays
 */
enum class WindowType {
    Compact,
    Medium,
    Expanded,
    Large,
    ExtraLarge
}