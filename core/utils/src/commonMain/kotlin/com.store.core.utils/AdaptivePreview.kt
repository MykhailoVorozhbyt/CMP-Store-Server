package com.store.core.utils

import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    device = Devices.PIXEL_9_PRO,
    name = "Phone preview Light"
)
@Preview(
    device = Devices.PIXEL_9_PRO,
    name = "Phone preview Dark",
    uiMode = UI_MODE_NIGHT_YES
)
annotation class PhonePreview

@Preview(
    device = Devices.PIXEL_3A_XL,
    name = "Small Phone preview Light",
    widthDp = 300,
    heightDp = 500,
    apiLevel = 34
)
@Preview(
    device = Devices.PIXEL_3A_XL,
    name = "Small Phone preview Dark",
    widthDp = 300,
    heightDp = 500,
    apiLevel = 34,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class SmallPhonePreview

@Preview(
    device = Devices.PIXEL_FOLD,
    name = "Foldable preview Light",
    apiLevel = 34
)
@Preview(
    device = Devices.PIXEL_FOLD,
    name = "Foldable preview Dark",
    apiLevel = 34,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class FoldablePreview

@Preview(
    device = Devices.PIXEL_TABLET,
    name = "Tablet preview Light",
    apiLevel = 34
)
@Preview(
    device = Devices.PIXEL_TABLET,
    name = "Tablet preview Dark",
    apiLevel = 34,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class TabletPreview

@Preview(
    device = Devices.DESKTOP,
    name = "Desktop preview Light",
    apiLevel = 34
)
@Preview(
    device = Devices.DESKTOP,
    name = "Desktop preview Dark",
    apiLevel = 34,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class DesktopPreview


@FoldablePreview
@TabletPreview
@DesktopPreview
annotation class LargeScreensPreview

@LargeScreensPreview
@PhonePreview
annotation class AdaptivePreview