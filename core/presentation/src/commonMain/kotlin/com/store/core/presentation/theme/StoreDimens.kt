package com.store.core.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalStoreDimens = staticCompositionLocalOf<StoreDimens> {
    error("LocalDimens no provided")
}

fun dimensFor(windowType: WindowType): StoreDimens = when (windowType) {
    WindowType.Compact -> CompactDimens
    WindowType.Medium -> MediumDimens
    WindowType.Expanded,
    WindowType.Large,
    WindowType.ExtraLarge -> ExpandedDimens
}

/**
 * @property iconButonSize for icon buton, this button can to contains icon
 * @property buttonIconSize for icon inside button
 * */
interface StoreDimens {
    // Padding
    val defaultPadding: Dp
    val spaceBetweenItems: Dp

    // Bottom Bar
    val bottomBarShape: Dp
    val bottomBarVerticalPadding: Dp
    val bottomBarHorizontalPadding: Dp

    // Drawer
    val drawerItemPadding: Dp

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
    val buttonRounded: Dp
    val buttonRoundedFull: Dp

    // Card

    // Text
    val textS: TextUnit
    val textM: TextUnit
    val textL: TextUnit
    val textXL: TextUnit
    val textField: TextUnit
    val textFieldRoundedCorner: Dp get() = 4.dp
    val textFieldVerticalPadding: Dp get() = 12.dp
    val textFieldHorizontalPadding: Dp get() = 8.dp

    val topAppBarText: TextUnit

    val customDrawerWidth: Float
    val customDrawerOffsetValue: Float
}

val CompactDimens = object : StoreDimens {
    override val defaultPadding: Dp get() = 20.dp
    override val spaceBetweenItems: Dp get() = 12.dp
    override val bottomBarShape: Dp get() = 24.dp
    override val bottomBarVerticalPadding: Dp get() = 24.dp
    override val bottomBarHorizontalPadding: Dp get() = 36.dp
    override val drawerItemPadding: Dp get() = 12.dp
    override val splashLogoSize: Dp get() = 240.dp
    override val iconSize: Dp get() = 24.dp
    override val iconLSize: Dp get() = 60.dp
    override val iconButonSize: Dp get() = 12.dp        // 14 → 12
    override val circularStrokeWidth: Dp get() = 2.dp
    override val buttonHeight: Dp get() = 52.dp         // 56 → 52
    override val buttonTextSize: TextUnit get() = 12.sp // 14 → 12
    override val buttonIconSize: Dp get() = 20.dp       // 24 → 20
    override val buttonBorder: Dp get() = 1.dp
    override val buttonRounded: Dp get() = 4.dp
    override val buttonRoundedFull: Dp get() = 99.dp
    override val textS: TextUnit get() = 10.sp          // 12 → 10
    override val textM: TextUnit get() = 12.sp          // 14 → 12
    override val textL: TextUnit get() = 14.sp          // 16 → 14
    override val textXL: TextUnit get() = 36.sp         // 40 → 36
    override val textField: TextUnit get() = 12.sp      // 14 → 12
    override val topAppBarText: TextUnit get() = 26.sp  // 30 → 26
    override val customDrawerWidth: Float get() = 0.5f
    override val customDrawerOffsetValue: Float get() = 1.5f
}
val MediumDimens = object : StoreDimens {
    override val defaultPadding: Dp get() = 20.dp
    override val spaceBetweenItems: Dp get() = 12.dp
    override val bottomBarShape: Dp get() = 24.dp
    override val bottomBarVerticalPadding: Dp get() = 24.dp
    override val bottomBarHorizontalPadding: Dp get() = 36.dp
    override val drawerItemPadding: Dp get() = 12.dp
    override val splashLogoSize: Dp get() = 240.dp
    override val iconSize: Dp get() = 24.dp
    override val iconLSize: Dp get() = 60.dp
    override val iconButonSize: Dp get() = 14.dp
    override val circularStrokeWidth: Dp get() = 2.dp
    override val buttonHeight: Dp get() = 56.dp
    override val buttonTextSize: TextUnit get() = 14.sp
    override val buttonIconSize: Dp get() = 24.dp
    override val buttonBorder: Dp get() = 1.dp
    override val buttonRounded: Dp get() = 4.dp
    override val buttonRoundedFull: Dp get() = 99.dp
    override val textS: TextUnit get() = 12.sp
    override val textM: TextUnit get() = 14.sp
    override val textL: TextUnit get() = 16.sp
    override val textXL: TextUnit get() = 40.sp
    override val textField: TextUnit get() = 14.sp
    override val topAppBarText: TextUnit get() = 30.sp
    override val customDrawerWidth: Float get() = 0.5f
    override val customDrawerOffsetValue: Float get() = 2f
}

val ExpandedDimens = object : StoreDimens {
    override val defaultPadding: Dp get() = 20.dp
    override val spaceBetweenItems: Dp get() = 14.dp
    override val bottomBarShape: Dp get() = 24.dp
    override val bottomBarVerticalPadding: Dp get() = 24.dp
    override val bottomBarHorizontalPadding: Dp get() = 36.dp
    override val drawerItemPadding: Dp get() = 12.dp
    override val splashLogoSize: Dp get() = 260.dp
    override val iconSize: Dp get() = 24.dp
    override val iconLSize: Dp get() = 60.dp
    override val iconButonSize: Dp get() = 14.dp
    override val circularStrokeWidth: Dp get() = 2.dp
    override val buttonHeight: Dp get() = 56.dp
    override val buttonTextSize: TextUnit get() = 14.sp
    override val buttonIconSize: Dp get() = 24.dp
    override val buttonBorder: Dp get() = 1.dp
    override val buttonRounded: Dp get() = 4.dp
    override val buttonRoundedFull: Dp get() = 99.dp
    override val textS: TextUnit get() = 12.sp
    override val textM: TextUnit get() = 14.sp
    override val textL: TextUnit get() = 16.sp
    override val textXL: TextUnit get() = 40.sp
    override val textField: TextUnit get() = 14.sp
    override val topAppBarText: TextUnit get() = 30.sp
    override val customDrawerWidth: Float get() = 0.25f
    override val customDrawerOffsetValue: Float get() = 5f
}