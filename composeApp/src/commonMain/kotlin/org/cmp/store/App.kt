package org.cmp.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.utils.AdaptivePreview

@Composable
fun App() {
    Column(
        modifier = Modifier
            .background(StoreTheme.color.window)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
    }
}

//@Composable
//fun AppScreenContents() {
//    when (rememberWindowType()) {
//        WindowType.Compact -> Content()
//        WindowType.Medium -> Content()
//        else -> ExpandedContent()
//    }
//}


@AdaptivePreview
@Composable
private fun AppPreview() {
    PreviewTheme {
        App()
    }
}