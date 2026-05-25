package com.store.core.presentation.ui.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults.colors
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.view_data.FieldError


@Composable
fun IndicatorLineWrap(
    enabled: Boolean,
    error: FieldError,
    interactionSource: MutableInteractionSource,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .indicatorLineModifier(
                enabled = enabled,
                error = error,
                interactionSource = interactionSource
            ),
        content = content
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Modifier.indicatorLineModifier(
    enabled: Boolean,
    error: FieldError,
    interactionSource: MutableInteractionSource
): Modifier {
    return this
        .indicatorLine(
            enabled = enabled,
            isError = error.hasError,
            colors = colors(
                focusedIndicatorColor = StoreTheme.color.inputBorderFocus,
                unfocusedIndicatorColor = StoreTheme.color.inputBorder,
                errorIndicatorColor = StoreTheme.color.inputBorderError
            ),
            interactionSource = interactionSource,
        )
}
