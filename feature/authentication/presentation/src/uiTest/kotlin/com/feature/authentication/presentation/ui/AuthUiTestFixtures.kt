package com.feature.authentication.presentation.ui

import com.feature.authentication.presentation.AuthenticationMockPreview
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.feature.authentication.presentation.view_data.ManualBlockViewData
import com.store.core.presentation.ui.base.view_data.InputFieldViewData

/**
 * Platform-agnostic test data for the authentication UI tests.
 *
 * Lives in commonTest, so the single [AuthenticationUITest] can be run per-method on both the
 * JVM/desktop (headless) and Android (emulator) targets from the IDE.
 */
object AuthUiTestFixtures {

    /** Default state: empty, not-yet-valid fields -> sign-in button disabled. */
    fun default(): AuthenticationViewData = AuthenticationMockPreview.getViewData()

    /** Both fields valid with no errors -> sign-in button enabled. */
    fun valid(): AuthenticationViewData = default().copy(manual = validManualBlock())

    /** Valid fields but a request in flight -> sign-in button disabled while loading. */
    fun validLoading(): AuthenticationViewData = valid().copy(isLoading = true)

    /** Only email valid, password empty/invalid -> sign-in button disabled. */
    fun onlyEmailValid(): AuthenticationViewData =
        default().copy(manual = ManualBlockViewData(email = validEmail()))

    /** Only password valid, email empty/invalid -> sign-in button disabled. */
    fun onlyPasswordValid(): AuthenticationViewData =
        default().copy(manual = ManualBlockViewData(password = validPassword()))

    private fun validManualBlock(): ManualBlockViewData = ManualBlockViewData(
        email = validEmail(),
        password = validPassword(),
    )

    private fun validEmail(): InputFieldViewData =
        InputFieldViewData(input = "user@store.com", isLocalValid = true)

    private fun validPassword(): InputFieldViewData =
        InputFieldViewData(input = "P@ssw0rd123", isLocalValid = true)
}
