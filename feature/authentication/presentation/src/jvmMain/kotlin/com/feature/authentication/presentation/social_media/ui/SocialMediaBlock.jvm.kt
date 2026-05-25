package com.feature.authentication.presentation.social_media.ui

import com.store.core.presentation.ui.ViewAction

@androidx.compose.runtime.Composable
actual fun PlatformGoogleButton(
    loading: Boolean,
    onViewAction: (ViewAction) -> Unit
) = GoogleButton(loading, onViewAction)