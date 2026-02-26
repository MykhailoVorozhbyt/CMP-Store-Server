package com.feature.authentication.presentation.social_media.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cmp_store_server.core.resources.generated.resources.Res
import cmp_store_server.core.resources.generated.resources.google_logo
import com.feature.authentication.presentation.social_media.SocialMediaBlockMockPreview
import com.feature.authentication.presentation.social_media.SocialMediaViewAction
import com.feature.authentication.presentation.social_media.view_data.SocialMediaBlockViewData
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.ViewAction
import com.store.core.utils.AdaptivePreview
import org.jetbrains.compose.resources.painterResource


@Composable
fun SocialMediaBlockContent(
    viewData: SocialMediaBlockViewData,
    modifier: Modifier = Modifier,
    onViewAction: (ViewAction) -> Unit
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoogleButton(loading = viewData.google.isLoading) {
            onViewAction.invoke(SocialMediaViewAction.OnGoogleClick)
        }
    }
}

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(size = StoreTheme.dimens.buttonRoundedFull)
    val primaryText = "Sign in with Google"
    val secondaryText = "Please wait..."
    var buttonText by remember { mutableStateOf(primaryText) }

    LaunchedEffect(loading) {
        buttonText = if (loading) secondaryText else primaryText
    }

    Surface(
        modifier = modifier
            .clip(shape)
            .border(
                width = StoreTheme.dimens.buttonBorder,
                color = StoreTheme.color.borderIdle,
                shape = shape
            )
            .clickable(enabled = !loading) { onClick() },
        color = StoreTheme.color.surfaceLight
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = StoreTheme.dimens.defaultPadding)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = loading
            ) { loadingState ->
                if (!loadingState) {
                    Icon(
                        painter = painterResource(Res.drawable.google_logo),
                        contentDescription = "Google Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(StoreTheme.dimens.buttonIconSize)
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(StoreTheme.dimens.buttonIconSize),
                        strokeWidth = StoreTheme.dimens.circularStrokeWidth,
                        color = StoreTheme.color.iconSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = buttonText,
                color = StoreTheme.color.textPrimary,
                fontSize = StoreTheme.dimens.buttonTextSize
            )
        }
    }
}

@AdaptivePreview
@Composable
private fun SocialMediaBlockContentPreview() {
    PreviewTheme {
        SocialMediaBlockContent(SocialMediaBlockMockPreview.getViewData()) {}
    }
}

@AdaptivePreview
@Composable
private fun SocialMediaBlockContentLoadingPreview() {
    PreviewTheme {
        SocialMediaBlockContent(SocialMediaBlockMockPreview.getLoadingViewData()) {}
    }
}
