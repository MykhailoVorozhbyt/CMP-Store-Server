package com.store.core.presentation.ui.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.PhonePreview
import com.store.core.resources.Resources
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Composable
fun StoreFilledButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loading: Boolean = false,
    enabled: Boolean = true,
    buttonColor: Color = StoreTheme.color.buttonPrimary,
    contentColor: Color = StoreTheme.color.textPrimary,
    textResource: StringResource,
    iconResource: DrawableResource? = null,
    shape: RoundedCornerShape = RoundedCornerShape(size = StoreTheme.dimens.buttonRounded),
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !loading && enabled,
        shape = shape,
        border = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = contentColor,
            disabledContainerColor = buttonColor.copy(alpha = 0.3f),
            disabledContentColor = contentColor.copy(alpha = 0.3f),
        ),
        elevation = null,
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        StoreButtonContent(
            loading = loading,
            textResource = textResource,
            iconResource = iconResource,
            contentColor = contentColor,
        )
    }
}

@PhonePreview
@Composable
private fun StoreFilledButtonPreview() {
    PreviewTheme {
        Column(
            modifier = Modifier.padding(StoreTheme.dimens.spaceBetweenItems),
            verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
        ) {
            StoreFilledButton(onClick = {}, textResource = StoreTheme.strings.appName)
            StoreFilledButton(
                onClick = {},
                textResource = StoreTheme.strings.signInWithGoogle,
                iconResource = Resources.Image.GoogleLogo,
            )
            StoreFilledButton(onClick = {}, loading = true, textResource = StoreTheme.strings.appName)
            StoreFilledButton(onClick = {}, enabled = false, textResource = StoreTheme.strings.appName)
        }
    }
}
