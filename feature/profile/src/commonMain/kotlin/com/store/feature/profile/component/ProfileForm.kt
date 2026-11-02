package com.store.feature.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.ViewAction
import com.store.core.presentation.ui.components.input.StoreTextField
import com.store.core.presentation.utils.NamedPreviewParameter
import com.store.core.presentation.utils.PhonePreview
import com.store.feature.profile.ProfilePreviewProvider
import com.store.feature.profile.resources.Res
import com.store.feature.profile.resources.profile_label_address
import com.store.feature.profile.resources.profile_label_city
import com.store.feature.profile.resources.profile_label_email
import com.store.feature.profile.resources.profile_label_first_name
import com.store.feature.profile.resources.profile_label_last_name
import com.store.feature.profile.resources.profile_label_phone_number
import com.store.feature.profile.resources.profile_label_postal_code
import com.store.feature.profile.view_data.ProfileFormViewData
import com.store.feature.profile.view_data.ProfileViewAction
import com.store.feature.profile.view_data.ProfileViewData
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileForm(
    viewData: ProfileFormViewData,
    enabled: Boolean,
    onViewAction: (ViewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val numberKeyboard = KeyboardOptions(keyboardType = KeyboardType.Number)
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems),
    ) {
        StoreTextField(
            viewData = viewData.firstName,
            hint = stringResource(Res.string.profile_label_first_name),
            enabled = enabled,
            onInputChanged = onViewAction,
        )
        StoreTextField(
            viewData = viewData.lastName,
            hint = stringResource(Res.string.profile_label_last_name),
            enabled = enabled,
            onInputChanged = onViewAction,
        )
        StoreTextField(
            viewData = viewData.email,
            hint = stringResource(Res.string.profile_label_email),
        )
        StoreTextField(
            viewData = viewData.city,
            hint = stringResource(Res.string.profile_label_city),
            enabled = enabled,
            onInputChanged = onViewAction,
        )
        StoreTextField(
            viewData = viewData.postalCode,
            hint = stringResource(Res.string.profile_label_postal_code),
            enabled = enabled,
            keyboardOptions = numberKeyboard,
            onInputChanged = onViewAction,
        )
        StoreTextField(
            viewData = viewData.address,
            hint = stringResource(Res.string.profile_label_address),
            enabled = enabled,
            onInputChanged = onViewAction,
        )
        PhoneTextField(
            viewData = viewData.phone,
            hint = stringResource(Res.string.profile_label_phone_number),
            enabled = enabled,
            onCountryClick = { onViewAction(ProfileViewAction.CountryPickerClicked) },
            onInputChanged = onViewAction,
        )
    }
}

@PhonePreview
@Composable
private fun ProfileFormPreview(
    @PreviewParameter(ProfilePreviewProvider::class) parameter: NamedPreviewParameter<ProfileViewData>,
) {
    PreviewTheme {
        ProfileForm(parameter.value.form, true, onViewAction = {})
    }
}

