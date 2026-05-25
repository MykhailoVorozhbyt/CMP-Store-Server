package com.store.core.presentation.ui.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.actions.InputFieldAction
import com.store.core.presentation.ui.base.actions.KeyboardAction
import com.store.core.presentation.ui.base.view_data.FieldError
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.asString
import com.store.core.presentation.utils.sanitizeString
import com.store.core.resources.Res
import com.store.core.resources.ic_warning
import com.store.core.presentation.utils.PhonePreview
import org.jetbrains.compose.resources.painterResource

@Composable
fun StoreTextField(
    viewData: InputFieldViewData,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelTextStyle: TextStyle = StoreTheme.typography.regular,
    hint: String = viewData.hint,
    error: FieldError = viewData.error,
    warningMessage: String = viewData.warningMessage.asString(),
    enabled: Boolean = viewData.enabled,
    singleLine: Boolean = true,
    regex: Regex? = viewData.config.regex,
    maxLength: Int = viewData.config.maxLength,
    testTag: String = "StoreTextField",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    decorationBox: @Composable ((innerTextField: @Composable () -> Unit) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onFocus: () -> Unit = {},
    onFocusLost: () -> Unit = {},
    onAction: (InputFieldAction) -> Unit = { _ -> },
    onInputChanged: (InputFieldChanged) -> Unit = onAction,
) {
    StoreTextField(
        hint = hint,
        text = viewData.inputState,
        error = error,
        warningMessage = warningMessage,
        enabled = enabled,
        modifier = modifier,
        label = label,
        labelTextStyle = labelTextStyle,
        interactionSource = interactionSource,
        singleLine = singleLine,
        regex = regex,
        maxLength = maxLength,
        testTag = testTag,
        controlByValidator = viewData.config.controlByValidator,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: KeyboardActions(
            onDone = { onAction(KeyboardAction.Done(viewData.field)) },
            onNext = { onAction(KeyboardAction.Next(viewData.field)) },
        ),
        visualTransformation = visualTransformation,
        decorationBox = decorationBox,
        onValueChange = { onInputChanged(InputFieldChanged(it, viewData.field)) },
        onFocus = onFocus,
        onFocusLost = {
            onInputChanged(InputFieldChanged(viewData.inputState.value, viewData.field, true))
            onFocusLost()
        }
    )
}

@Composable
fun StoreTextField(
    modifier: Modifier = Modifier,
    label: String? = null,
    labelTextStyle: TextStyle = StoreTheme.typography.regular,
    hint: String? = null,
    text: MutableState<String>,
    error: FieldError = FieldError.NO_ERROR,
    warningMessage: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    regex: Regex? = null,
    controlByValidator: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    testTag: String = "StoreTextField",
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    decorationBox: @Composable ((innerTextField: @Composable () -> Unit) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onValueChange: (String) -> Unit = {},
    onFocus: () -> Unit = {},
    onFocusLost: () -> Unit = {}
) {
    val errorMessage = error.errorMessage.asString()

    var isFocused by remember { mutableStateOf(false) }
    val textFieldValue = rememberTextFieldValue(text.value, isFocused)
    val internalOnValueChange = internalOnValueChange(
        textFieldValue = textFieldValue,
        text = text,
        regex = regex,
        maxLength = maxLength,
        controlByValidator = controlByValidator,
        onValueChange = onValueChange
    )

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                style = labelTextStyle,
                color = StoreTheme.color.inputLabelTxt,
            )
        }
        BasicTextField(
            value = textFieldValue.value,
            onValueChange = internalOnValueChange,
            textStyle = StoreTheme.typography.textField.copy(color = StoreTheme.color.inputLabelTxt),
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            cursorBrush = SolidColor(StoreTheme.color.inputLabelTxt),
            decorationBox = { innerTextField ->
                IndicatorLineWrap(
                    enabled = enabled,
                    error = error,
                    interactionSource = interactionSource
                ) {
                    if (decorationBox != null) decorationBox(innerTextField)
                    else DefaultDecorationBox(innerTextField, hint, text, enabled)
                }
            },
            modifier = Modifier
                .padding(top = if (label != null) 4.dp else 0.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (isFocused && !focusState.isFocused) {
                        onFocusLost.invoke()
                    } else if (focusState.isFocused) {
                        val len = text.value.length
                        textFieldValue.value = textFieldValue.value.copy(selection = TextRange(len))
                        onFocus.invoke()
                    }
                    isFocused = focusState.isFocused
                }
                .testTag(testTag),
            interactionSource = interactionSource
        )
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                style = StoreTheme.typography.regular,
                color = StoreTheme.color.inputErrorTxt,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (warningMessage.isNotEmpty()) {
            WarningMessage(warningMessage)
        }
    }
}

@Composable
private fun WarningMessage(warningMessage: String) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp, top = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_warning),
            tint = StoreTheme.color.notificationWarningBg,
            contentDescription = null
        )
        Text(
            text = warningMessage,
            style = StoreTheme.typography.regular,
            color = StoreTheme.color.inputHelpTxt,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun rememberTextFieldValue(
    text: String,
    isFocused: Boolean
): MutableState<TextFieldValue> {
    val textFieldState = remember {
        mutableStateOf(TextFieldValue(text, TextRange(text.length)))
    }
    LaunchedEffect(text, isFocused) {
        if (textFieldState.value.text != text) {
            textFieldState.value = TextFieldValue(text, TextRange(text.length))
        }
    }
    return textFieldState
}

@Composable
private fun internalOnValueChange(
    textFieldValue: MutableState<TextFieldValue>,
    text: MutableState<String>,
    regex: Regex? = null,
    maxLength: Int = Int.MAX_VALUE,
    controlByValidator: Boolean = false,
    onValueChange: (String) -> Unit,
): (TextFieldValue) -> Unit = remember(regex, maxLength, onValueChange, text, textFieldValue) {

    { newValue: TextFieldValue ->
        val candidate = newValue.text
        val isLikelyPaste = candidate.length - textFieldValue.value.text.length > 1
        if (isLikelyPaste) {
            // Updating textFieldValue and text state only with sanitized or ignoring if nothing valid
            val sanitized = sanitizeString(newValue.text, regex, maxLength)
            if (sanitized.isEmpty()) return@remember // Do nothing if nothing valid to paste

            textFieldValue.value = if (sanitized == newValue.text) newValue
            else TextFieldValue(sanitized, TextRange(sanitized.length))

            text.value = sanitized
            onValueChange(sanitized)
        } else {
            if (candidate.length >= maxLength) return@remember
            // Normal input, updating if valid per rules
            if (regex == null || regex.matches(candidate)) {
                textFieldValue.value = newValue
                if (text.value != candidate) {
                    text.value = candidate
                    onValueChange(candidate)
                }
            } else if (controlByValidator) {
                // let validator decide what to do
                onValueChange(candidate)
            }
        }
    }

}


@PhonePreview
@Composable
private fun StoreTextFieldPreview() {
    PreviewTheme {
        /**@onFocusLost**/
        StoreTextField(
            label = "Something about what to write",
            text = remember { mutableStateOf("Text") },
            error = FieldError(true),
            enabled = true,
            warningMessage = "Warning Message!",
            onFocusLost = { /**@onFocusLost**/ }
        )
    }
}

@PhonePreview
@Composable
private fun StoreTextFieldDisablePreview() {
    PreviewTheme {
        /**@onFocusLost**/
        StoreTextField(
            label = "Something about what to write",
            text = remember { mutableStateOf("Text") },
            error = FieldError(true),
            enabled = false,
            warningMessage = "Warning Message!",
            onFocusLost = { /**@onFocusLost**/ }
        )
    }
}
