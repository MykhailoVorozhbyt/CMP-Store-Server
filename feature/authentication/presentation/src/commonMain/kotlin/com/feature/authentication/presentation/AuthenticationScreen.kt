package com.feature.authentication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.feature.authentication.presentation.social_media.ui.SocialMediaBlockContent
import com.feature.authentication.presentation.view_data.AuthenticationViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.presentation.ui.base.MessageEventData
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.base.collectEventsWithDefaultProcessing
import com.store.core.presentation.ui.components.StoreSnackbar
import com.store.core.presentation.ui.components.StoreSnackbarHostState
import com.store.core.utils.AdaptivePreview
import com.store.core.utils.Alpha
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel = koinViewModel(),
    navigateToHome: (String) -> Unit
) {
    val viewData by viewModel.viewDataState.collectAsState()
    val snackBarState = remember { StoreSnackbarHostState() }
    viewModel.collectEventsWithDefaultProcessing(
        snackbarHostState = snackBarState,
        processCustom = { event, defaultProcess ->
            when (event) {
                is AuthenticationViewAction.ToMainScreen -> navigateToHome(event.welcomeMessage)
                event -> defaultProcess(event)
            }
        })
    AuthenticationContent(viewData, viewModel::onViewAction)
    StoreSnackbar(snackBarState, snackbarBoxModifier = Modifier.statusBarsPadding())
}

@Composable
fun AuthenticationContent(
    viewData: AuthenticationViewData,
    onViewAction: (ViewAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(StoreTheme.color.window)
            .padding(StoreTheme.dimens.defaultPadding)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(StoreTheme.strings.appName),
                textAlign = TextAlign.Center,
                style = StoreTheme.typography.bxl,
                color = StoreTheme.color.textSecondary
            )
            Text(
                modifier = Modifier.fillMaxWidth().alpha(Alpha.HALF),
                text = stringResource(StoreTheme.strings.signInText),
                textAlign = TextAlign.Center,
                style = StoreTheme.typography.rl,
                color = StoreTheme.color.textPrimary
            )
        }
        SocialMediaBlockContent(viewData.socialMedia, onViewAction = onViewAction)
    }
}

@AdaptivePreview
@Composable
fun AuthenticationScreenPreview() {
    PreviewTheme {
        AuthenticationContent(AuthenticationMockPreview.getViewData()) {}
    }
}