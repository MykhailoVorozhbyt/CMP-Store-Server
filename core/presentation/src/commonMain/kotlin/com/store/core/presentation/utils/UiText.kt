package com.store.core.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Immutable
sealed interface UiText {
    data object Empty : UiText
    data class Resource(val value: StringResource) : UiText
    data class ResourceArgs(
        val value: StringResource,
        val formatArgs: List<Any>
    ) : UiText {
        companion object {
            operator fun invoke(
                value: StringResource,
                vararg formatArgs: Any
            ): ResourceArgs = ResourceArgs(value, formatArgs.toList())
        }
    }

    data class Raw(val value: String) : UiText
}

suspend fun UiText?.resolve(): String = when (this) {
    null, UiText.Empty -> ""
    is UiText.Raw -> value
    is UiText.Resource -> getString(value)
    is UiText.ResourceArgs -> getString(value, *formatArgs.toTypedArray())
}

@Composable
fun UiText?.asString(): String = when (this) {
    null, UiText.Empty -> ""
    is UiText.Raw -> value
    is UiText.Resource -> stringResource(value)
    is UiText.ResourceArgs -> stringResource(value, *formatArgs.toTypedArray())
}
