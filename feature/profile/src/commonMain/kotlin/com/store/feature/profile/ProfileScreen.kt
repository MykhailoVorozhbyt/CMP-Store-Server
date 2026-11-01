package com.store.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.store.core.navigation.LocalAppNavigator
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.base.collectEventsWithDefaultProcessing
import com.store.core.presentation.ui.components.StoreSnackbar
import com.store.core.presentation.ui.components.StoreSnackbarHostState
import com.store.core.presentation.ui.components.button.StoreOutlinedButton
import com.store.core.presentation.utils.NamedPreviewParameter
import com.store.core.presentation.utils.PhonePreview
import com.store.core.presentation.utils.ScreenState
import com.store.core.resources.Resources
import com.store.core.resources.back_description
import com.store.core.resources.common_error_no_internet
import com.store.feature.profile.components.CountryPickerDialog
import com.store.feature.profile.components.ProfileErrorCard
import com.store.feature.profile.components.ProfileForm
import com.store.feature.profile.resources.Res
import com.store.feature.profile.resources.profile_error_description
import com.store.feature.profile.resources.profile_title
import com.store.feature.profile.resources.profile_update
import com.store.feature.profile.view_data.ProfileViewAction
import com.store.feature.profile.view_data.ProfileViewData
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import com.store.core.resources.Res as CoreRes

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val viewData by viewModel.viewDataState.collectAsStateWithLifecycle()
    val snackBarState = remember { StoreSnackbarHostState() }
    val navigator = LocalAppNavigator.current
    viewModel.collectEventsWithDefaultProcessing(snackbarHostState = snackBarState)
    ProfileContent(
        viewData = viewData,
        onBackClick = { navigator.back() },
        onViewAction = viewModel::onViewAction,
    )
    StoreSnackbar(snackBarState, snackbarBoxModifier = Modifier.statusBarsPadding())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileContent(
    viewData: ProfileViewData,
    onBackClick: () -> Unit,
    onViewAction: (ViewAction) -> Unit,
) {
    Scaffold(
        containerColor = StoreTheme.color.window,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.profile_title),
                        style = StoreTheme.typography.topAppBar,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = stringResource(CoreRes.string.back_description),
                            tint = StoreTheme.color.iconPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = StoreTheme.color.window,
                    titleContentColor = StoreTheme.color.textPrimary,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(StoreTheme.dimens.defaultPadding),
            contentAlignment = Alignment.Center,
        ) {
            when (viewData.screenState) {
                ScreenState.Loading -> CircularProgressIndicator(color = StoreTheme.color.textSecondary)
                ScreenState.Error -> ProfileErrorCard(message = stringResource(Res.string.profile_error_description))
                ScreenState.NoInternetConnection -> ProfileErrorCard(
                    message = stringResource(
                        CoreRes.string.common_error_no_internet
                    )
                )

                ScreenState.Success -> ProfileLoadedContent(viewData, onViewAction)
            }
        }
    }
    viewData.countryPicker?.let { picker ->
        CountryPickerDialog(viewData = picker, onViewAction = onViewAction)
    }
}

@Composable
private fun ProfileLoadedContent(
    viewData: ProfileViewData,
    onViewAction: (ViewAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
    ) {
        ProfileForm(
            modifier = Modifier.weight(1f),
            viewData = viewData.form,
            enabled = !viewData.isUpdating,
            onViewAction = onViewAction,
        )
        StoreOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            textResource = Res.string.profile_update,
            iconResource = Resources.Icon.Checkmark,
            loading = viewData.isUpdating,
            enabled = viewData.updateEnabled,
            onClick = { onViewAction(ProfileViewAction.UpdateClicked) },
        )
    }
}

@PhonePreview
@Composable
private fun ProfileContentPreview(
    @PreviewParameter(ProfilePreviewProvider::class) parameter: NamedPreviewParameter<ProfileViewData>,
) {
    PreviewTheme {
        ProfileContent(parameter.value, onBackClick = {}) {}
    }
}

@PhonePreview
@Composable
private fun ProfileLoadedContentPreview() {
    PreviewTheme {
        Box(
            modifier = Modifier.background(StoreTheme.color.window)
                .padding(StoreTheme.dimens.defaultPadding)
        ) {
            ProfileLoadedContent(
                viewData = ProfileViewData(screenState = ScreenState.Success, isUpdating = true),
                onViewAction = {},
            )
        }
    }
}
