package com.store.feature.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.components.StoreShimmer
import com.store.core.presentation.utils.PhonePreview

private const val PROFILE_FORM_FIELD_COUNT = 7
private val TextFieldShimmerHeight = 46.dp

@Composable
internal fun Shimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(StoreTheme.dimens.defaultPadding),
        verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
        ) {
            repeat(PROFILE_FORM_FIELD_COUNT) {
                StoreShimmer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TextFieldShimmerHeight)
                        .clip(RoundedCornerShape(StoreTheme.dimens.textFieldRoundedCorner)),
                )
            }
        }
        StoreShimmer(
            modifier = Modifier
                .fillMaxWidth()
                .height(StoreTheme.dimens.buttonHeight)
                .clip(RoundedCornerShape(StoreTheme.dimens.buttonRounded)),
        )
    }
}

@PhonePreview
@Composable
private fun ShimmerPreview() {
    PreviewTheme {
        Box(
            modifier = Modifier.background(StoreTheme.color.backgroundMain)
        ) {
            Shimmer()
        }
    }
}
