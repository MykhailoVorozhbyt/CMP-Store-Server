package com.store.component.auth.data.mappers

import com.store.component.auth.model.AuthError
import com.store.core.domain.DataError
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthErrorMapperTest {

    @Test
    fun auth_codes_become_auth_specific_errors() {
        assertEquals(AuthError.InvalidCredentials, NetworkError.INVALID_CREDENTIALS.toAuthError())
        assertEquals(AuthError.AccountHasNoPassword, NetworkError.ACCOUNT_HAS_NO_PASSWORD.toAuthError())
        assertEquals(AuthError.UserAlreadyExists, NetworkError.USER_ALREADY_EXISTS.toAuthError())
        assertEquals(AuthError.EmailRequired, NetworkError.EMAIL_REQUIRED.toAuthError())
        assertEquals(AuthError.PasswordRequired, NetworkError.PASSWORD_REQUIRED.toAuthError())
        assertEquals(AuthError.ProviderUserIdRequired, NetworkError.PROVIDER_USER_ID_REQUIRED.toAuthError())
        assertEquals(AuthError.ProviderNotSupported, NetworkError.AUTH_PROVIDER_NOT_SUPPORTED.toAuthError())
    }

    @Test
    fun every_other_code_is_delegated_to_the_common_data_error() {
        assertEquals(AuthError.Common(DataError.NoInternet), NetworkError.NO_INTERNET.toAuthError())
        assertEquals(AuthError.Common(DataError.Unauthorized), NetworkError.TOKEN_REUSE_DETECTED.toAuthError())
        assertEquals(AuthError.Common(DataError.Server), NetworkError.SERVER_ERROR.toAuthError())
    }
}
