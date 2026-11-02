package com.store.feature.profile.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.components.input.StoreTextField
import com.store.core.presentation.utils.MediumPhoneFoldablePreview
import com.store.core.resources.Resources
import com.store.core.resources.common_cancel
import com.store.core.resources.common_confirm
import com.store.feature.profile.resources.Res
import com.store.feature.profile.resources.profile_country_dialog_hint
import com.store.feature.profile.resources.profile_country_dialog_title
import com.store.feature.profile.resources.profile_country_item
import com.store.feature.profile.resources.profile_country_not_found
import com.store.feature.profile.view_data.Country
import com.store.feature.profile.view_data.CountryPickerViewData
import com.store.feature.profile.view_data.ProfileViewAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.store.core.resources.Res as CoreRes

@Composable
internal fun CountryPickerDialog(
    viewData: CountryPickerViewData,
    onViewAction: (ViewAction) -> Unit,
) {
    AlertDialog(
        modifier = Modifier.padding(StoreTheme.dimens.dialogPadding),
        containerColor = StoreTheme.color.backgroundSoft,
        onDismissRequest = { onViewAction(ProfileViewAction.CountryPickerDismissed) },
        title = {
            Text(
                text = stringResource(Res.string.profile_country_dialog_title),
                style = StoreTheme.typography.bl,
                color = StoreTheme.color.textPrimary,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems)) {
                StoreTextField(
                    viewData = viewData.query,
                    hint = stringResource(Res.string.profile_country_dialog_hint),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onInputChanged = onViewAction,
                )
                val countries = viewData.countries
                if (countries.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        text = stringResource(Res.string.profile_country_not_found),
                        style = StoreTheme.typography.rm,
                        color = StoreTheme.color.textPrimary,
                    )
                } else {
                    countries.forEach { country ->
                        CountryItem(
                            country = country,
                            isSelected = country == viewData.selected,
                            onClick = { onViewAction(ProfileViewAction.CountryHighlighted(country)) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = viewData.confirmEnabled,
                onClick = { onViewAction(ProfileViewAction.CountryConfirmed) },
            ) {
                Text(
                    stringResource(CoreRes.string.common_confirm),
                    color = StoreTheme.color.textSecondary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onViewAction(ProfileViewAction.CountryPickerDismissed) }) {
                Text(
                    stringResource(CoreRes.string.common_cancel),
                    color = StoreTheme.color.textPrimary
                )
            }
        },
    )
}

@Composable
private fun CountryItem(
    country: Country,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(StoreTheme.dimens.textFieldRoundedCorner),
        color = StoreTheme.color.backgroundMain,
        border = BorderStroke(
            width = StoreTheme.dimens.buttonBorder,
            color = if (isSelected) StoreTheme.color.textSecondary else StoreTheme.color.borderIdle,
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                modifier = Modifier.size(StoreTheme.dimens.iconSize),
                painter = painterResource(country.flag),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(
                    Res.string.profile_country_item,
                    country.dialCode,
                    stringResource(country.title)
                ),
                style = StoreTheme.typography.rm,
                color = StoreTheme.color.textPrimary,
            )
            if (isSelected) {
                Icon(
                    modifier = Modifier.size(StoreTheme.dimens.buttonIconSize),
                    painter = painterResource(Resources.Icon.Checkmark),
                    contentDescription = null,
                    tint = StoreTheme.color.textSecondary,
                )
            }
        }
    }
}

@MediumPhoneFoldablePreview
@Composable
private fun CountryItemPreview() {
    PreviewTheme {
        Column(
            modifier = Modifier
                .background(StoreTheme.color.surface)
                .padding(StoreTheme.dimens.spaceBetweenItems),
            verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
        ) {
            CountryItem(country = Country.Ukraine, isSelected = true, onClick = {})
            CountryItem(country = Country.Usa, isSelected = false, onClick = {})
        }
    }
}
