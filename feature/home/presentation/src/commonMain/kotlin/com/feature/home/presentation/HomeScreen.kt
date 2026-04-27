package com.feature.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.feature.home.presentation.view_data.HomeViewData
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.base.MessageEventData
import com.store.core.presentation.ui.base.collectEventsWithDefaultProcessing
import com.store.core.presentation.ui.components.StoreSnackbar
import com.store.core.presentation.ui.components.StoreSnackbarHostState
import com.store.core.utils.AdaptivePreview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    welcomeMessage: String? = null,
    viewModel: HomeViewModel = koinViewModel()
) {
    val viewData by viewModel.viewDataState.collectAsStateWithLifecycle()
    val snackBarState = remember { StoreSnackbarHostState() }
    viewModel.collectEventsWithDefaultProcessing(snackbarHostState = snackBarState)

    LaunchedEffect(welcomeMessage) {
        welcomeMessage?.let { snackBarState.show(MessageEventData.success(it)) }
    }

    HomeContent(
        viewData = viewData,
        onViewAction = viewModel::onViewAction,
    )
    StoreSnackbar(snackBarState)
}

@Composable
internal fun HomeContent(
    viewData: HomeViewData,
    onViewAction: (ViewAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(StoreTheme.dimens.defaultPadding)
    ) {
        Text("HomeScreen")
    }
}

@AdaptivePreview
@Composable
private fun HomeContentPreview() {
    PreviewTheme {
        HomeContent(HomeMockPreview.getViewData()) {}
    }
}