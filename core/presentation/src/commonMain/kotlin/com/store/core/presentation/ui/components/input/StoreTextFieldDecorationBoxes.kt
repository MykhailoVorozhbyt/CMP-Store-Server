package com.store.core.presentation.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.core.presentation.theme.StoreTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DefaultDecorationBox(
    innerTextField: @Composable () -> Unit,
    hint: String? = null,
    text: MutableState<String>,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier.textFieldSurfaceWithPadding(enabled = enabled)
    ) {
        if (hint != null && text.value.isEmpty()) {
            Text(
                text = hint,
                style = StoreTheme.typography.rm,
                color = StoreTheme.color.inputTxtPlaceholder,
            )
        }
        innerTextField()
    }
}

@Composable
fun DecorationBoxWithUnitHint(
    innerTextField: @Composable () -> Unit,
    text: MutableState<String>,
    unitHint: String,
    hint: String? = null,
    enabled: Boolean = true,
) {
    Box(
        modifier = Modifier.textFieldSurfaceWithPadding(enabled = enabled)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (hint != null && text.value.isEmpty()) {
                    Text(
                        text = hint,
                        style = StoreTheme.typography.regular,
                        fontSize = 14.sp,
                        color = StoreTheme.color.inputTxtPlaceholder,
                    )
                }
                innerTextField()
            }
            Text(
                text = unitHint,
                style = StoreTheme.typography.rm,
                color = StoreTheme.color.inputTxtPlaceholder,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun DecorationBoxWithTrailingIcon(
    modifier: Modifier = Modifier,
    innerTextField: @Composable () -> Unit,
    text: MutableState<String>,
    drawableResource: DrawableResource,
    iconSize: Dp = 16.dp,
    tint: Color = StoreTheme.color.inputTxt,
    onIconClick: (() -> Unit)? = null,
    iconContentDescription: String? = null,
    hint: String? = null,
    enabled: Boolean = true,
) {
    DecorationBoxWithIcons(
        modifier = modifier,
        innerTextField = innerTextField,
        text = text,
        hint = hint,
        enabled = enabled,
        trailingIcon = {
            Icon(
                painter = painterResource(drawableResource),
                tint = tint,
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .size(iconSize)
                    .then(
                        if (onIconClick != null) {
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onIconClick()
                            }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    )
}

@Composable
fun DecorationBoxWithIcons(
    modifier: Modifier = Modifier,
    innerTextField: @Composable () -> Unit,
    text: MutableState<String>,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    hint: String? = null,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier.textFieldSurfaceWithPadding(enabled = enabled)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            leadingIcon?.let {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    leadingIcon()
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                if (hint != null && text.value.isEmpty()) {
                    Text(
                        text = hint,
                        style = StoreTheme.typography.regular,
                        fontSize = 14.sp,
                        color = StoreTheme.color.inputTxtPlaceholder,
                    )
                }
                innerTextField()
            }
            trailingIcon?.let {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    trailingIcon()
                }
            }
        }
    }
}

@Composable
fun Modifier.textFieldSurface(
    enabled: Boolean
): Modifier {
    return this
        .fillMaxWidth()
        .background(
            color = if (enabled) StoreTheme.color.inputBg else StoreTheme.color.inputBgDisable,
            shape = RoundedCornerShape(size = StoreTheme.dimens.textFieldRoundedCorner)
        )
        .border(
            width = 1.dp,
            color = StoreTheme.color.inputBorder,
            shape = RoundedCornerShape(size = StoreTheme.dimens.textFieldRoundedCorner)
        )
}

@Composable
fun Modifier.textFieldPadding(): Modifier {
    return this.padding(
        vertical = StoreTheme.dimens.textFieldVerticalPadding,
        horizontal = StoreTheme.dimens.textFieldHorizontalPadding
    )
}

@Composable
fun Modifier.textFieldSurfaceWithPadding(
    enabled: Boolean
): Modifier = this
    .textFieldSurface(enabled)
    .textFieldPadding()
