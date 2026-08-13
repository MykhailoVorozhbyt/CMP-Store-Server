package com.feature.authentication.presentation.social_media.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.presentation.AuthenticationTags
import com.feature.authentication.presentation.social_media.SocialMediaViewAction
import com.feature.authentication.presentation.social_media.view_data.SocialMediaBlockViewData
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.components.button.StoreOutlinedButton
import com.store.core.resources.Resources

@Composable
internal fun SocialMediaBlockContent(
    viewData: SocialMediaBlockViewData,
    modifier: Modifier = Modifier,
    onViewAction: (ViewAction) -> Unit
) {
    val isPreview = LocalInspectionMode.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPreview) {
            GoogleButton(viewData.google.isLoading, onViewAction)
        } else {
            PlatformGoogleButton(viewData.google.isLoading, onViewAction)
        }
    }
}

@Composable
expect fun PlatformGoogleButton(
    loading: Boolean = false,
    onViewAction: (ViewAction) -> Unit
)

@Composable
internal fun MobileGoogleButtonUiContainerFirebase(
    loading: Boolean,
    onViewAction: (ViewAction) -> Unit
) {
    GoogleButtonUiContainerFirebase(
        linkAccount = false,
        onResult = { result ->
            result.onSuccess { user ->
                onViewAction(
                    SocialMediaViewAction.OnSignInSuccess(user)
                )
            }.onFailure { error ->
                onViewAction(
                    SocialMediaViewAction.OnSignInFailure(
                        GoogleSignInError.Unknown(
                            error
                        )
                    )
                )
            }
        }
    ) {
        GoogleButton(loading = loading) {
            onViewAction(SocialMediaViewAction.OnGoogleClick)
            this@GoogleButtonUiContainerFirebase.onClick()
        }
    }
}

@Composable
internal fun GoogleButton(
    loading: Boolean = false,
    onViewAction: (ViewAction) -> Unit
) {
    val buttonTextId = if (loading) {
        StoreTheme.strings.pleaseWait
    } else {
        StoreTheme.strings.signInWithGoogle
    }
    StoreOutlinedButton(
        modifier = Modifier.testTag(AuthenticationTags.GOOGLE_BUTTON),
        onClick = {
            onViewAction(SocialMediaViewAction.OnGoogleClick)
        },
        loading = loading,
        textResource = buttonTextId,
        iconResource = Resources.Image.GoogleLogo
    )
}