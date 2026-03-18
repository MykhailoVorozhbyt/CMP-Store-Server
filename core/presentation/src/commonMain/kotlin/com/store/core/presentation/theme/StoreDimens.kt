package com.store.core.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalStoreDimens = staticCompositionLocalOf<StoreDimens> {
    error("LocalDimens no provided")
}

@Composable
fun rememberDimens(): StoreDimens {
    return when (rememberWindowType()) {
        WindowType.Compact -> CompactDimens
        WindowType.Medium -> MediumDimens
        WindowType.Expanded -> ExpandedDimens
        WindowType.Large -> ExpandedDimens
        WindowType.ExtraLarge -> ExpandedDimens
    }
}

/**
 * @param iconButonSize for icon buton, this button can contains icon
 * @param buttonIconSize for icon inside button
 * */
interface StoreDimens {
    // Padding
    val defaultPadding: Dp
    val spaceBetweenItems: Dp

    // Splash Logo Size
    val splashLogoSize: Dp

    // Icon
    val iconSize: Dp
    val iconLSize: Dp
    val iconButonSize: Dp

    // CircularStrokeWidth
    val circularStrokeWidth: Dp

    // Button
    val buttonHeight: Dp
    val buttonTextSize: TextUnit
    val buttonIconSize: Dp
    val buttonBorder: Dp
    val buttonRoundedFull: Dp

    // Card

    // Text
    val textS: TextUnit
    val textM: TextUnit
    val textL: TextUnit
    val textXL: TextUnit
    val textField: TextUnit
    val topAppBarText: TextUnit
}

val CompactDimens = object : StoreDimens {
    override val defaultPadding: Dp get() = 20.dp
    override val splashLogoSize: Dp get() = 240.dp
    override val spaceBetweenItems: Dp get() = 12.dp
    override val iconSize: Dp get() = 24.dp
    override val iconLSize: Dp get() = 60.dp
    override val iconButonSize: Dp get() = 14.dp
    override val circularStrokeWidth: Dp get() = 2.dp
    override val buttonHeight: Dp get() = 56.dp
    override val buttonTextSize: TextUnit get() = 14.sp
    override val buttonIconSize: Dp get() = 24.dp
    override val buttonBorder: Dp get() = 1.dp
    override val buttonRoundedFull: Dp get() = 99.dp
    override val textS: TextUnit get() = 12.sp
    override val textM: TextUnit get() = 14.sp
    override val textL: TextUnit get() = 16.sp
    override val textXL: TextUnit get() = 40.sp
    override val textField: TextUnit get() = 14.sp
    override val topAppBarText: TextUnit get() = 30.sp
}

val MediumDimens = object : StoreDimens {
    override val defaultPadding: Dp get() = 20.dp
    override val splashLogoSize: Dp get() = 240.dp
    override val spaceBetweenItems: Dp get() = 12.dp
    override val iconSize: Dp get() = 24.dp
    override val iconLSize: Dp get() = 60.dp
    override val iconButonSize: Dp get() = 14.dp
    override val circularStrokeWidth: Dp get() = 2.dp
    override val buttonHeight: Dp get() = 56.dp
    override val buttonTextSize: TextUnit get() = 14.sp
    override val buttonIconSize: Dp get() = 24.dp
    override val buttonBorder: Dp get() = 1.dp
    override val buttonRoundedFull: Dp get() = 99.dp
    override val textS: TextUnit get() = 12.sp
    override val textM: TextUnit get() = 14.sp
    override val textL: TextUnit get() = 16.sp
    override val textXL: TextUnit get() = 40.sp
    override val textField: TextUnit get() = 14.sp
    override val topAppBarText: TextUnit get() = 30.sp
}

val ExpandedDimens = object : StoreDimens {
    override val defaultPadding: Dp get() = 20.dp
    override val splashLogoSize: Dp get() = 260.dp
    override val spaceBetweenItems: Dp get() = 14.dp
    override val iconSize: Dp get() = 24.dp
    override val iconLSize: Dp get() = 60.dp
    override val iconButonSize: Dp get() = 14.dp
    override val circularStrokeWidth: Dp get() = 2.dp
    override val buttonHeight: Dp get() = 56.dp
    override val buttonTextSize: TextUnit get() = 14.sp
    override val buttonIconSize: Dp get() = 24.dp
    override val buttonBorder: Dp get() = 1.dp
    override val buttonRoundedFull: Dp get() = 99.dp
    override val textS: TextUnit get() = 12.sp
    override val textM: TextUnit get() = 14.sp
    override val textL: TextUnit get() = 16.sp
    override val textXL: TextUnit get() = 40.sp
    override val textField: TextUnit get() = 14.sp
    override val topAppBarText: TextUnit get() = 30.sp
}