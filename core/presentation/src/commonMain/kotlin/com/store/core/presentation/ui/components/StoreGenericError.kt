package com.store.core.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.store.core.presentation.resources.Res
import com.store.core.presentation.resources.generic_error_message
import com.store.core.presentation.resources.generic_error_title
import com.store.core.presentation.resources.generic_try_again_button
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.components.button.StoreFilledButton
import com.store.core.presentation.utils.PhonePreview
import com.store.core.resources.Resources
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoreGenericError(
    onClick: () -> Unit,
    message: StringResource,
    modifier: Modifier = Modifier,
    title: StringResource? = null,
) {
    StoreGenericError(
        onClick = onClick,
        title = title?.let { stringResource(it) },
        message = stringResource(message),
        modifier = modifier,
    )
}

@Composable
fun StoreGenericError(
    onClick: () -> Unit,
    icon: DrawableResource? = null,
    title: String? = null,
    message: String? = null,
    showAppName: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(StoreTheme.dimens.defaultPadding),
        verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
    ) {
        if (showAppName) {
            Text(
                text = stringResource(StoreTheme.strings.appName),
                style = StoreTheme.typography.bl,
                color = StoreTheme.color.textSecondary,
            )
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                StoreTheme.dimens.spaceBetweenItems,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (icon != null) {
                Image(
                    modifier = Modifier.size(StoreTheme.dimens.splashLogoSize / 2),
                    painter = painterResource(icon),
                    contentDescription = null,
                )
            }
            if (title != null) {
                Text(
                    text = title,
                    style = StoreTheme.typography.bl,
                    color = StoreTheme.color.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
            if (message != null) {
                Text(
                    text = message,
                    style = StoreTheme.typography.rm,
                    color = StoreTheme.color.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }
        StoreFilledButton(
            modifier = Modifier.fillMaxWidth(),
            textResource = Res.string.generic_try_again_button,
            buttonColor = StoreTheme.color.brandMain,
            onClick = onClick,
        )
    }
}

@PhonePreview
@Composable
private fun StoreGenericErrorResourcePreview() {
    PreviewTheme {
        StoreGenericError(
            onClick = {},
            modifier = Modifier.background(StoreTheme.color.backgroundMain),
            title = Res.string.generic_error_title,
            message = Res.string.generic_error_message,
        )
    }
}

@PhonePreview
@Composable
private fun StoreGenericErrorPreview() {
    PreviewTheme {
        StoreGenericError(
            onClick = {},
            modifier = Modifier.background(StoreTheme.color.backgroundMain),
            title = "Oops!",
            message = "Something went wrong.",
        )
    }
}
