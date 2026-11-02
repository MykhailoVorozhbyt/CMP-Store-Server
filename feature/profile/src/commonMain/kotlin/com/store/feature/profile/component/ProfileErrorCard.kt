package com.store.feature.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.resources.Resources
import com.store.feature.profile.resources.Res
import com.store.feature.profile.resources.profile_error_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileErrorCard(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(StoreTheme.dimens.splashLogoSize / 2),
            painter = painterResource(Resources.Image.Cat),
            contentDescription = null,
        )
        Text(
            text = stringResource(Res.string.profile_error_title),
            style = StoreTheme.typography.bl,
            color = StoreTheme.color.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = StoreTheme.typography.rm,
            color = StoreTheme.color.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun ProfileErrorCardPreview() {
    PreviewTheme {
        ProfileErrorCard(
            modifier = Modifier.background(StoreTheme.color.backgroundMain),
            message = "Something went wrong while loading your profile.",
        )
    }
}
