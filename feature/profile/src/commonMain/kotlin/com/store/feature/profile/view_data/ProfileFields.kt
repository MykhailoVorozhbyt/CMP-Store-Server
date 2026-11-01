package com.store.feature.profile.view_data

import com.store.core.presentation.ui.base.Field

sealed class ProfileFields : Field {
    data object FirstName : ProfileFields()
    data object LastName : ProfileFields()
    data object Email : ProfileFields()
    data object City : ProfileFields()
    data object PostalCode : ProfileFields()
    data object Address : ProfileFields()
    data object PhoneNumber : ProfileFields()
    data object DialCodeSearch : ProfileFields()
}
