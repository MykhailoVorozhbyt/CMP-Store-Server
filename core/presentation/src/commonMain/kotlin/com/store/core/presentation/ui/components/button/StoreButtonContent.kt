package com.store.core.presentation.ui.components.button

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.resources.Resources
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RowScope.StoreButtonContent(
    loading: Boolean,
    textResource: StringResource,
    iconResource: DrawableResource?,
    contentColor: Color,
) {
    AnimatedContent(targetState = loading) { loadingState ->
        when {
            loadingState -> CircularProgressIndicator(
                modifier = Modifier.size(StoreTheme.dimens.buttonIconSize),
                strokeWidth = StoreTheme.dimens.circularStrokeWidth,
                color = StoreTheme.color.iconSecondary,
            )

            iconResource != null -> Icon(
                painter = painterResource(iconResource),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(StoreTheme.dimens.buttonIconSize),
            )

            else -> Unit
        }
    }
    Spacer(Modifier.width(4.dp))
    Text(
        text = stringResource(textResource),
        color = contentColor,
        fontSize = StoreTheme.dimens.buttonTextSize,
    )
}

@Preview
@Composable
private fun StoreButtonContentPreview() {
    PreviewTheme {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StoreButtonContent(
                loading = false,
                textResource = StoreTheme.strings.signInWithGoogle,
                iconResource = Resources.Image.GoogleLogo,
                contentColor = StoreTheme.color.textPrimary,
            )
        }
    }
}
