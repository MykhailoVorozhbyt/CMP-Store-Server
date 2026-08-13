package com.feature.authentication.presentation.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import com.feature.authentication.presentation.AuthenticationContent
import com.feature.authentication.presentation.AuthenticationTags
import com.feature.authentication.presentation.view_data.AuthenticationViewAction
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.ui.ViewAction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Compose UI test for the authentication screen.
 *
 * Single copy in commonTest, runnable per-method on both targets from the IDE: headless on the
 * JVM/desktop (`jvmTest`, no emulator) and as an Android instrumented test (`androidDeviceTest`,
 * emulator). Each target supplies its own `runComposeUiTest` runner via the module plugin.
 */
@OptIn(ExperimentalTestApi::class)
class AuthenticationUITest {

    @Test
    fun allInputsAndButtonsAreDisplayed() = runComposeUiTest {
        renderAuth(AuthUiTestFixtures.default())

        onNodeWithTag(AuthenticationTags.EMAIL_FIELD).assertIsDisplayed()
        onNodeWithTag(AuthenticationTags.PASSWORD_FIELD).assertIsDisplayed()
        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).assertIsDisplayed()
        onNodeWithTag(AuthenticationTags.GOOGLE_BUTTON).assertIsDisplayed()
    }

    @Test
    fun signInButtonIsDisabledWhenInputsAreInvalid() = runComposeUiTest {
        renderAuth(AuthUiTestFixtures.default())

        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun signInButtonIsDisabledWhenOnlyEmailIsValid() = runComposeUiTest {
        renderAuth(AuthUiTestFixtures.onlyEmailValid())

        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun signInButtonIsDisabledWhenOnlyPasswordIsValid() = runComposeUiTest {
        renderAuth(AuthUiTestFixtures.onlyPasswordValid())

        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun signInButtonIsEnabledWhenEmailAndPasswordAreValid() = runComposeUiTest {
        renderAuth(AuthUiTestFixtures.valid())

        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).assertIsEnabled()
    }

    @Test
    fun signInButtonEnabledWhenValid_andClickDispatchesOnSignInClick() = runComposeUiTest {
        val dispatched = mutableListOf<ViewAction>()

        renderAuth(AuthUiTestFixtures.valid(), onAction = { dispatched += it })

        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).assertIsEnabled()
        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).performClick()

        assertTrue(
            dispatched.contains(AuthenticationViewAction.OnSignInClick),
            "Clicking the sign-in button should dispatch OnSignInClick, but got: $dispatched"
        )
        assertEquals(1, dispatched.size)
    }

    @Test
    fun signInButtonIsDisabledWhileLoading() = runComposeUiTest {
        renderAuth(AuthUiTestFixtures.validLoading())

        onNodeWithTag(AuthenticationTags.SIGN_IN_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun typingIntoEmailAndPasswordFieldsShowsEnteredText() = runComposeUiTest {
        val email = "user@store.com"
        val password = "P@ssw0rd123"
        renderAuth(AuthUiTestFixtures.default())

        onNodeWithTag(AuthenticationTags.EMAIL_FIELD).performTextInput(email)
        onNodeWithTag(AuthenticationTags.EMAIL_FIELD).assertTextEquals(email)

        onNodeWithTag(AuthenticationTags.PASSWORD_FIELD).performTextInput(password)
        onNodeWithTag(AuthenticationTags.PASSWORD_FIELD)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.InputText,
                    AnnotatedString(password)
                )
            )
            .assertTextEquals("\u2022".repeat(password.length))
    }

    private fun ComposeUiTest.renderAuth(
        viewData: AuthenticationViewData,
        onAction: (ViewAction) -> Unit = {}
    ) = setContent {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewTheme {
                AuthenticationContent(viewData = viewData, onViewAction = onAction)
            }
        }
    }
}
