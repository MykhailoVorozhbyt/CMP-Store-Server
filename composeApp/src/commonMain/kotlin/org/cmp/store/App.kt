package org.cmp.store

import androidx.compose.runtime.Composable
import com.store.core.navigation.SetupNavGraph
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.utils.AdaptivePreview

@Composable
fun App() {
    SetupNavGraph()
}

@AdaptivePreview
@Composable
private fun AppPreview() {
    PreviewTheme {
        App()
    }
}