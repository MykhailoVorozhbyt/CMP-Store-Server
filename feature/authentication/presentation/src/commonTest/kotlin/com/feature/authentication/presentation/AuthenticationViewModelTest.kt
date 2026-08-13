package com.feature.authentication.presentation

import app.cash.turbine.test
import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.domain.usecases.SignInUseCase
import com.store.test.fakes.FakeAuthRepository
import com.feature.authentication.presentation.fakes.FakeGoogleSignInService
import com.feature.authentication.presentation.fakes.FakeSignInFailureHandler
import com.feature.authentication.presentation.validator.AuthenticationValidator
import com.feature.authentication.presentation.view_data.AuthenticationFields
import com.feature.authentication.presentation.view_data.AuthenticationUiEvent
import com.feature.authentication.presentation.view_data.AuthenticationViewAction
import com.store.core.domain.model.validation.email.EmailDomainValidationConfig
import com.store.core.domain.model.validation.email.EmailDomainValidator
import com.store.core.presentation.core.NotificationType
import com.store.core.presentation.ui.base.UiEvent
import com.store.core.presentation.utils.UiText
import com.store.core.presentation.validation.email.EmailFieldValidator
import com.store.core.presentation.validation.email.EmailPatternValidator
import com.store.core.presentation.validation.password.PasswordFieldValidator
import com.store.core.resources.Res
import com.store.core.resources.auth_success
import com.store.core.resources.auth_success_already_registered
import com.store.test.BaseViewModelTest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.auth.response.AuthResponse
import org.cmp.store.domain.customer.Customer
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest : BaseViewModelTest() {

    private val authRepository = FakeAuthRepository()
    private val googleSignInService = FakeGoogleSignInService()

    // region _viewData

    @Test
    fun initial_state_has_loading_off_and_configured_manual_fields() = runVmTest {
        val viewModel = buildViewModel()

        val state = viewModel.viewDataState.value
        assertFalse(state.isLoading)
        assertFalse(state.socialMedia.google.isLoading)
        assertEquals(AuthenticationFields.Email, state.manual.email.field)
        assertEquals(AuthenticationFields.Password, state.manual.password.field)
    }

    @Test
    fun manual_sign_in_flips_isLoading_true_while_in_flight_then_false() = runVmTest {
        val gate = CompletableDeferred<Unit>()
        authRepository.gate = gate
        authRepository.authorizeResult = ApiResult.Success(authResponse(isNewAccount = true))
        val viewModel = buildViewModel()

        viewModel.onViewAction(AuthenticationViewAction.OnSignInClick)
        advanceUntilIdle() // runs up to the suspended authorize() gate
        assertTrue(
            viewModel.viewDataState.value.isLoading,
            "loading should be on while authorizing"
        )

        gate.complete(Unit)
        advanceUntilIdle()
        assertFalse(
            viewModel.viewDataState.value.isLoading,
            "loading should reset after result"
        )
    }

    @Test
    fun manual_sign_in_disables_fields_while_in_flight_then_re_enables() = runVmTest {
        val gate = CompletableDeferred<Unit>()
        authRepository.gate = gate
        authRepository.authorizeResult = ApiResult.Success(authResponse(isNewAccount = true))
        val viewModel = buildViewModel()

        viewModel.onViewAction(AuthenticationViewAction.OnSignInClick)
        advanceUntilIdle() // suspends at the authorize() gate — still loading

        val inFlight = viewModel.viewDataState.value.manual
        assertFalse(inFlight.email.enabled, "email should be disabled while authorizing")
        assertFalse(inFlight.password.enabled, "password should be disabled while authorizing")

        gate.complete(Unit)
        advanceUntilIdle()

        val settled = viewModel.viewDataState.value.manual
        assertTrue(settled.email.enabled, "email should re-enable after result")
        assertTrue(settled.password.enabled, "password should re-enable after result")
    }

    @Test
    fun manual_sign_in_resets_loading_flags_on_InvalidCredentials() = runVmTest {
        authRepository.authorizeResult = ApiResult.Error(NetworkError.INVALID_CREDENTIALS)
        val viewModel = buildViewModel()

        viewModel.onViewAction(AuthenticationViewAction.OnSignInClick)
        advanceUntilIdle()

        val state = viewModel.viewDataState.value
        assertFalse(state.isLoading)
        assertFalse(state.socialMedia.google.isLoading)
    }

    // endregion

    // region uiEvents

    @Test
    fun successful_new_account_emits_ToMain_with_success_message() = runVmTest {
        authRepository.authorizeResult = ApiResult.Success(authResponse(isNewAccount = true))
        val viewModel = buildViewModel()

        viewModel.uiEvents.test {
            viewModel.onViewAction(AuthenticationViewAction.OnSignInClick)

            assertEquals(
                AuthenticationUiEvent.ToMain(UiText.Resource(Res.string.auth_success)),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun existing_account_emits_to_main_with_already_registered_message() = runVmTest {
        authRepository.authorizeResult = ApiResult.Success(authResponse(isNewAccount = false))
        val viewModel = buildViewModel()

        viewModel.uiEvents.test {
            viewModel.onViewAction(AuthenticationViewAction.OnSignInClick)

            assertEquals(
                AuthenticationUiEvent.ToMain(UiText.Resource(Res.string.auth_success_already_registered)),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun generic_failure_emits_an_error_ShowMessage() = runVmTest {
        authRepository.authorizeResult = ApiResult.Error(NetworkError.SERVER_ERROR)
        val viewModel = buildViewModel()

        viewModel.uiEvents.test {
            viewModel.onViewAction(AuthenticationViewAction.OnSignInClick)
            advanceUntilIdle()

            val event = awaitItem()
            assertIs<UiEvent.ShowMessage>(event)
            assertEquals(NotificationType.ERROR, event.data.type)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun google_sign_in_failure_resets_loading_and_emits_an_error_event() = runVmTest {
        val viewModel = buildViewModel()
        googleSignInService.signInResult =
            ApiResult.Error(GoogleSignInError.Unknown(IllegalStateException("google down")))

        viewModel.uiEvents.test {
            viewModel.googleSignIn()

            val event = awaitItem()
            assertIs<UiEvent.ShowMessage>(event)
            assertEquals(NotificationType.ERROR, event.data.type)
            assertFalse(viewModel.viewDataState.value.socialMedia.google.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    private fun buildViewModel(): AuthenticationViewModel = AuthenticationViewModel(
        signInUseCase = SignInUseCase(authRepository),
        googleSignInService = googleSignInService,
        signInFailureHandler = FakeSignInFailureHandler(),
        validator = AuthenticationValidator(
            emailValidator = EmailFieldValidator(
                emailValidator = EmailPatternValidator(),
                emailDomainValidator = EmailDomainValidator(EmailDomainValidationConfig.Impl())
            ),
            passwordValidator = PasswordFieldValidator()
        ),
        dispatchers = dispatchers
    )

    private fun authResponse(
        isNewAccount: Boolean,
        provider: AuthProvider = AuthProvider.MANUAL,
    ) = AuthResponse(
        accessToken = "token",
        refreshToken = "refresh-token",
        customer = Customer(
            id = "customer-id",
            firstName = "First",
            lastName = "Last",
            email = "user@example.com"
        ),
        isNewAccount = isNewAccount,
        provider = provider,
    )
}
