package com.feature.home.presentation

import androidx.compose.runtime.Composable
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.utils.AdaptivePreview

@AdaptivePreview
@Composable
private fun HomeContentPreview() {
    PreviewTheme {
        HomeContent(HomeMockPreview.getViewData()) {}
    }
}