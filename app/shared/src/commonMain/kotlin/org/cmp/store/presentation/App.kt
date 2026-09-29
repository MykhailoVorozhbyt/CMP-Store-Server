package org.cmp.store.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.store.core.navigation.SetupNavGraph
import com.store.core.presentation.theme.BaseTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    viewModel: AppViewModel = koinViewModel()
) {
    BaseTheme {
        val viewData by viewModel.viewDataState.collectAsState()

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = viewData.appReady
        ) {
            SetupNavGraph(viewData.startDestination)
        }
    }
}
