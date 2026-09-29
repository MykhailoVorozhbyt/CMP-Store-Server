package com.store.feature.authentication.social_media.ui

import com.store.core.presentation.ui.ViewAction

@androidx.compose.runtime.Composable
actual fun PlatformGoogleButton(
    loading: Boolean,
    onViewAction: (ViewAction) -> Unit
) = MobileGoogleButtonUiContainerFirebase(loading, onViewAction)