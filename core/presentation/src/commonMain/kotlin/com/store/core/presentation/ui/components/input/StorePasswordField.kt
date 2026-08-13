package com.store.core.presentation.ui.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.view_data.FieldError
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.resources.Res
import com.store.core.resources.hide_password_description
import com.store.core.resources.ic_hide
import com.store.core.resources.ic_show
import com.store.core.resources.show_password_description
import com.store.core.presentation.utils.CompactPreview
import org.jetbrains.compose.resources.stringResource

@Composable
fun PasswordField(
    inputData: InputFieldViewData,
    modifier: Modifier = Modifier,
    hint: String = inputData.hint,
    error: FieldError = inputData.error,
    enabled: Boolean = inputData.enabled,
    label: String? = null,
    testTag: String = "PasswordField",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onInputChanged: (InputFieldChanged) -> Unit = {},
    onFocus: () -> Unit = {},
    onFocusLost: () -> Unit = {}
) {
    var hidePassword by remember {
        mutableStateOf(true)
    }
    val visualTransformation = remember { PasswordVisualTransformation() }

    StoreTextField(
        modifier = modifier,
        label = label,
        hint = hint,
        viewData = inputData,
        error = error,
        enabled = enabled,
        maxLength = inputData.config.maxLength,
        testTag = testTag,
        visualTransformation = if (hidePassword) visualTransformation else VisualTransformation.None,
        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Password),
        keyboardActions = keyboardActions,
        onFocus = onFocus,
        onFocusLost = onFocusLost,
        onInputChanged = onInputChanged,
        decorationBox = { innerTextField ->
            DecorationBoxWithTrailingIcon(
                innerTextField = innerTextField,
                text = inputData.inputState,
                hint = hint,
                enabled = enabled,
                drawableResource = if (hidePassword) Res.drawable.ic_hide else Res.drawable.ic_show,
                iconContentDescription = stringResource(
                    if (hidePassword) Res.string.show_password_description
                    else Res.string.hide_password_description
                ),
                tint = StoreTheme.color.inputTxt,
                onIconClick = {
                    hidePassword = !hidePassword
                }
            )
        },
    )
}

@CompactPreview
@Composable
private fun PasswordFieldPreview() {
    PreviewTheme {
        PasswordField(
            label = "Something about what to write",
            inputData = InputFieldViewData().apply {
                input = "Pass"
            },
            error = FieldError(true),
            enabled = true,
            onFocusLost = { /**@onFocusLost**/ },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@CompactPreview
@Composable
private fun PasswordFieldDisablePreview() {
    PreviewTheme {
        PasswordField(
            label = "Something about what to write",
            inputData = InputFieldViewData().apply {
                input = "Pass"
            },
            error = FieldError(true),
            enabled = false,
            onFocusLost = { /**@onFocusLost**/ },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
