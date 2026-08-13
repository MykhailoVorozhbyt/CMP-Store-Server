package com.store.core.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.resources.Res
import com.store.core.resources.common_or
import com.store.core.presentation.utils.PhonePreview
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextDivider(
    modifier: Modifier = Modifier,
    text: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(color = StoreTheme.color.textPrimary, modifier = Modifier.weight(1f))
        text()
        HorizontalDivider(color = StoreTheme.color.textPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TextDivider(
    modifier: Modifier = Modifier,
    text: StringResource
) {
    TextDivider(modifier) {
        Text(
            text = stringResource(text),
            style = StoreTheme.typography.bs,
            color = StoreTheme.color.textPrimary
        )
    }
}

@PhonePreview
@Composable
private fun TextDividerPrev() {
    PreviewTheme {
        TextDivider(text = Res.string.common_or)
    }
}