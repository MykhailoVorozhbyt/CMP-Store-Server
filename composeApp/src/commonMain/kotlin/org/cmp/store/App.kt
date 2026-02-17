package org.cmp.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.store.core.presentation.theme.BasePreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.theme.WindowType
import com.store.core.presentation.theme.rememberWindowType
import com.store.core.resources.Res
import com.store.core.resources.compose_multiplatform
import com.store.core.utils.AdaptivePreview
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    Column(
        modifier = Modifier
            .background(StoreTheme.color.window)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppScreenContents()
    }
}

@Composable
fun AppScreenContents() {
    when (rememberWindowType()) {
        WindowType.Compact -> Content()
        WindowType.Medium -> Content()
        else -> ExpandedContent()
    }
}

@Composable
private fun Content() {
    val greeting = remember { Greeting().greet() }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painterResource(Res.drawable.compose_multiplatform), null)
        Text("Compose: $greeting")
    }
}

@Composable
private fun ExpandedContent() {
    val greeting = remember { Greeting().greet() }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(painterResource(Res.drawable.compose_multiplatform), null)
        Text("Compose: $greeting")
    }
}


@AdaptivePreview
@Composable
private fun AppPreview() {
    BasePreviewTheme {
        App()
    }
}