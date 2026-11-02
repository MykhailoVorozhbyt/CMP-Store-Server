package com.store.feature.profile.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.feature.profile.view_data.Country
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val FlagSize = 16.dp

@Composable
internal fun DialCodeButton(
    country: Country,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = null,
                indication = ripple(bounded = false),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            modifier = Modifier.size(FlagSize),
            painter = painterResource(country.flag),
            contentDescription = stringResource(country.title),
        )
        Text(
            text = "+${country.dialCode}",
            style = StoreTheme.typography.textField,
            color = StoreTheme.color.inputTxt,
        )
        VerticalDivider(
            modifier = Modifier.height(FlagSize),
            color = StoreTheme.color.inputBorder,
        )
    }
}

@Preview
@Composable
private fun DialCodeButtonPreview() {
    PreviewTheme {
        Column(
            modifier = Modifier
                .background(StoreTheme.color.surface)
                .padding(StoreTheme.dimens.spaceBetweenItems),
            verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
        ) {
            Country.entries.forEach { country ->
                DialCodeButton(country = country, enabled = true, onClick = {})
            }
        }
    }
}
