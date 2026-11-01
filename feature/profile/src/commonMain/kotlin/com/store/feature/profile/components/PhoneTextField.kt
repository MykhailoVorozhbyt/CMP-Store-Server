package com.store.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.ui.components.input.DecorationBoxWithIcons
import com.store.core.presentation.ui.components.input.StoreTextField
import com.store.feature.profile.view_data.Country
import com.store.feature.profile.view_data.PhoneFieldViewData

@Composable
internal fun PhoneTextField(
    viewData: PhoneFieldViewData,
    hint: String,
    enabled: Boolean,
    onCountryClick: () -> Unit,
    onInputChanged: (InputFieldChanged) -> Unit,
    modifier: Modifier = Modifier,
) {
    StoreTextField(
        modifier = modifier,
        viewData = viewData.number,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onInputChanged = onInputChanged,
        decorationBox = { innerTextField ->
            DecorationBoxWithIcons(
                innerTextField = innerTextField,
                text = viewData.number.inputState,
                hint = hint,
                enabled = enabled,
                leadingIcon = {
                    DialCodeButton(country = viewData.country, enabled = enabled, onClick = onCountryClick)
                },
            )
        },
    )
}

@Preview
@Composable
private fun PhoneTextFieldPreview() {
    PreviewTheme {
        Column(
            modifier = Modifier
                .background(StoreTheme.color.surface)
                .padding(StoreTheme.dimens.spaceBetweenItems),
            verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
        ) {
            PhoneTextField(
                viewData = remember { PhoneFieldViewData(country = Country.Usa) },
                hint = "Phone number",
                enabled = true,
                onCountryClick = {},
                onInputChanged = {},
            )
            PhoneTextField(
                viewData = remember {
                    PhoneFieldViewData(number = InputFieldViewData(input = "123456789"), country = Country.Ukraine)
                },
                hint = "Phone number",
                enabled = true,
                onCountryClick = {},
                onInputChanged = {},
            )
            PhoneTextField(
                viewData = remember {
                    PhoneFieldViewData(number = InputFieldViewData(input = "123456789"), country = Country.Ukraine)
                },
                hint = "Phone number",
                enabled = false,
                onCountryClick = {},
                onInputChanged = {},
            )
        }
    }
}
