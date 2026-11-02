package com.store.core.presentation.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.resources.Resources
import com.store.core.presentation.utils.PhonePreview
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Composable
fun StoreOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean = false,
    enabled: Boolean = true,
    buttonColor: Color = StoreTheme.color.surface,
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
            containerColor = buttonColor,
            contentColor = StoreTheme.color.textPrimary,
            disabledContainerColor = buttonColor.copy(alpha = 0.3f),
            disabledContentColor = StoreTheme.color.textPrimary.copy(alpha = 0.3f),
        ),
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        StoreButtonContent(
            loading = loading,
            textResource = textResource,
            iconResource = iconResource,
            contentColor = StoreTheme.color.textPrimary,
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