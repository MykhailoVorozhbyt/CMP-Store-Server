package com.store.core.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.store.core.presentation.theme.WindowType.Compact
import com.store.core.presentation.theme.WindowType.Expanded
import com.store.core.presentation.theme.WindowType.ExtraLarge
import com.store.core.presentation.theme.WindowType.Large
import com.store.core.presentation.theme.WindowType.Medium

val LocalStoreWindowType = staticCompositionLocalOf<WindowType> {
    error("LocalStoreWindowType no provided")
}

fun windowTypeFromWidth(widthDp: Float): WindowType = when {
    widthDp < 600 -> Compact
    widthDp < 840 -> Medium
    widthDp < 1200 -> Expanded
    widthDp < 1600 -> Large
    else -> ExtraLarge
}

/**
 * Represents width-based window size classes.
 * Breakpoints:
 * @see Compact:    width < 600dp
 *      ~99% of phones in portrait
 * @see Medium:     600dp ≤ width < 840dp
 *      Most tablets in portrait
 *      Large foldables in portrait
 * @see Expanded:   840dp ≤ width < 1200dp
 *      Tablets in landscape
 *      Large unfolded foldables in landscape
 * @see Large:      1200dp ≤ width < 1600dp
 *      Large tablet displays
 * @see ExtraLarge: width ≥ 1600dp
 *      Desktop displays
 */
enum class WindowType {
    Compact,
    Medium,
    Expanded,
    Large,
    ExtraLarge
}