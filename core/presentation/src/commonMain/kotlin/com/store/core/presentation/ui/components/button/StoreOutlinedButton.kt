package com.store.core.presentation.ui.components.button

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.resources.Resources
import com.store.core.presentation.utils.PhonePreview
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoreOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean = false,
    enabled: Boolean = true,
    textResource: StringResource,
    iconResource: DrawableResource? = null,
    shape: RoundedCornerShape = RoundedCornerShape(size = StoreTheme.dimens.buttonRounded)
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        enabled = !loading && enabled,
        shape = shape,
        border = BorderStroke(
            width = StoreTheme.dimens.buttonBorder,
            color = StoreTheme.color.borderIdle,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = StoreTheme.color.surfaceLight,
            contentColor = StoreTheme.color.textPrimary,
            disabledContainerColor = StoreTheme.color.surfaceLight.copy(alpha = 0.3f),
            disabledContentColor = StoreTheme.color.textPrimary.copy(alpha = 0.3f),
        ),
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        AnimatedContent(
            targetState = loading
        ) { loadingState ->
            when {
                iconResource != null && loadingState.not() -> {
                    Icon(
                        painter = painterResource(iconResource),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(StoreTheme.dimens.buttonIconSize)
                    )
                }

                loadingState -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(StoreTheme.dimens.buttonIconSize),
                        strokeWidth = StoreTheme.dimens.circularStrokeWidth,
                        color = StoreTheme.color.iconSecondary
                    )
                }

                else -> Unit
            }
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(textResource),
            color = StoreTheme.color.textPrimary,
            fontSize = StoreTheme.dimens.buttonTextSize
        )
    }
}

@PhonePreview
@Composable
fun StoreOutlinedWithIconPreview() {
    PreviewTheme {
        StoreOutlinedButton(
            onClick = {},
            textResource = StoreTheme.strings.signInWithGoogle,
            iconResource = Resources.Image.GoogleLogo,
        )
    }
}

@PhonePreview
@Composable
fun StoreOutlinedWithIconLoadingPreview() {
    PreviewTheme {
        StoreOutlinedButton(
            onClick = {},
            loading = true,
            textResource = StoreTheme.strings.signInWithGoogle,
            iconResource = Resources.Image.GoogleLogo,
        )
    }
}

@PhonePreview
@Composable
fun StoreOutlinedPreview() {
    PreviewTheme {
        StoreOutlinedButton(
            onClick = {},
            textResource = StoreTheme.strings.appName
        )
    }
}

@PhonePreview
@Composable
fun StoreOutlinedLoadingPreview() {
    PreviewTheme {
        StoreOutlinedButton(
            onClick = {},
            loading = true,
            textResource = StoreTheme.strings.appName
        )
    }
}