package com.feature.authentication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.feature.authentication.presentation.social_media.ui.SocialMediaBlockContent
import com.feature.authentication.presentation.view_data.AuthenticationUiEvent
import com.feature.authentication.presentation.view_data.AuthenticationViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.feature.authentication.presentation.view_data.ManualBlockViewData
import com.store.core.navigation.LocalAppNavigator
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.base.collectEventsWithDefaultProcessing
import com.store.core.presentation.ui.components.StoreSnackbar
import com.store.core.presentation.ui.components.StoreSnackbarHostState
import com.store.core.presentation.ui.components.TextDivider
import com.store.core.presentation.ui.components.button.StoreOutlinedButton
import com.store.core.presentation.ui.components.input.PasswordField
import com.store.core.presentation.ui.components.input.StoreTextField
import com.store.core.presentation.utils.resolve
import com.store.core.resources.Res
import com.store.core.resources.auth_hint_enter_email
import com.store.core.resources.auth_hint_enter_password
import com.store.core.resources.auth_lable_enter_email
import com.store.core.resources.auth_lable_enter_password
import com.store.core.resources.auth_sign_in
import com.store.core.resources.common_or
import com.store.core.utils.Alpha
import com.store.core.presentation.utils.PhonePreview
import com.store.core.presentation.navigation.Screen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel = koinViewModel()
) {
    val viewData by viewModel.viewDataState.collectAsStateWithLifecycle()
    val snackBarState = remember { StoreSnackbarHostState() }
    val navigator = LocalAppNavigator.current
    viewModel.collectEventsWithDefaultProcessing(
        snackbarHostState = snackBarState,
        processCustom = { event, defaultProcess ->
            when (event) {
                is AuthenticationUiEvent.ToMain -> navigator.replaceAll(Screen.HomeGraph(event.welcomeMessage.resolve()))
                event -> defaultProcess(event)
            }
        })
    AuthenticationContent(viewData, viewModel::onViewAction)
    StoreSnackbar(snackBarState, snackbarBoxModifier = Modifier.statusBarsPadding())
}

@Composable
internal fun AuthenticationContent(
    viewData: AuthenticationViewData,
    onViewAction: (ViewAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(StoreTheme.color.window)
            .padding(StoreTheme.dimens.defaultPadding),
        verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems)
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
        ManualEmail(viewData.manual, viewData.isLoading, onViewAction)
        TextDivider(text = Res.string.common_or)
        SocialMediaBlockContent(viewData.socialMedia, onViewAction = onViewAction)
    }
}

@Composable
private fun ManualEmail(
    viewData: ManualBlockViewData,
    isLoading: Boolean,
    onViewAction: (ViewAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StoreTextField(
            viewData = viewData.email,
            label = stringResource(Res.string.auth_lable_enter_email),
            hint = stringResource(Res.string.auth_hint_enter_email),
            testTag = AuthenticationTags.EMAIL_FIELD,
            onInputChanged = onViewAction
        )
        PasswordField(
            inputData = viewData.password,
            label = stringResource(Res.string.auth_lable_enter_password),
            hint = stringResource(Res.string.auth_hint_enter_password),
            testTag = AuthenticationTags.PASSWORD_FIELD,
            onInputChanged = onViewAction
        )
        StoreOutlinedButton(
            modifier = Modifier.testTag(AuthenticationTags.SIGN_IN_BUTTON),
            textResource = Res.string.auth_sign_in,
            loading = isLoading,
            enabled = viewData.buttonEnabled,
            onClick = {
                onViewAction(AuthenticationViewAction.OnSignInClick)
            }
        )
    }
}

@PhonePreview
@Composable
private fun AuthenticationScreenPreview() {
    PreviewTheme {
        AuthenticationContent(AuthenticationMockPreview.getViewData()) {}
    }
}
