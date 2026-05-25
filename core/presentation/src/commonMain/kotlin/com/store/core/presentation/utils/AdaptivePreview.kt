package com.store.core.presentation.utils

import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

private const val API = 34

// Compact (< 600dp) — typical phone portrait
@Preview(device = Devices.PHONE, name = "Compact · Light")
@Preview(device = Devices.PHONE, name = "Compact · Dark", uiMode = UI_MODE_NIGHT_YES)
annotation class CompactPreview

// Medium (600–839dp) — phone landscape / small tablet portrait
@Preview(device = Devices.FOLDABLE, name = "Medium · Light", apiLevel = API)
@Preview(
    device = Devices.FOLDABLE,
    name = "Medium · Dark",
    apiLevel = API,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class MediumPreview

// Expanded (840–1199dp) — tablet landscape / large foldable (no reference device)
@Preview(widthDp = 1000, heightDp = 800, name = "Expanded · Light", apiLevel = API)
@Preview(
    widthDp = 1000,
    heightDp = 800,
    name = "Expanded · Dark",
    apiLevel = API,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class ExpandedPreview

// Large (1200–1599dp)
@Preview(device = Devices.TABLET, name = "Large · Light", apiLevel = API)
@Preview(device = Devices.TABLET, name = "Large · Dark", apiLevel = API, uiMode = UI_MODE_NIGHT_YES)
annotation class LargePreview

// ExtraLarge (≥ 1600dp) — desktop
@Preview(device = Devices.DESKTOP, name = "ExtraLarge · Light", apiLevel = API)
@Preview(
    device = Devices.DESKTOP,
    name = "ExtraLarge · Dark",
    apiLevel = API,
    uiMode = UI_MODE_NIGHT_YES
)
annotation class ExtraLargePreview

@CompactPreview
@MediumPreview
annotation class PhonePreview

@ExpandedPreview
@LargePreview
@ExtraLargePreview
annotation class LargeScreensPreview

@PhonePreview
@LargeScreensPreview
annotation class AdaptivePreview