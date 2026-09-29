package com.store.feature.authentication.view_data

import com.store.core.presentation.ui.base.Field

sealed class AuthenticationFields : Field {
    data object Email : AuthenticationFields()
    data object Password : AuthenticationFields()
}