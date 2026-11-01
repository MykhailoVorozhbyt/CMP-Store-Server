package com.store.feature.profile.view_data

import androidx.compose.runtime.Immutable
import com.store.core.presentation.ui.base.view_data.InputFieldConfig
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.intRegexRules

@Immutable
data class PhoneFieldViewData(
    val number: InputFieldViewData = InputFieldViewData(
        field = ProfileFields.PhoneNumber,
        config = InputFieldConfig(regex = intRegexRules),
    ),
    val country: Country = Country.Default,
)
