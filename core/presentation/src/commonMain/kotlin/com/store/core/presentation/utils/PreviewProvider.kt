package com.store.core.presentation.utils

import androidx.compose.runtime.Immutable
import androidx.compose.ui.tooling.preview.PreviewParameterProvider


@Immutable
data class NamedPreviewParameter<T>(
    val name: String,
    val value: T,
)

abstract class PreviewProvider<T> : PreviewParameterProvider<NamedPreviewParameter<T>> {

    protected abstract val parameters: List<NamedPreviewParameter<T>>

    override val values: Sequence<NamedPreviewParameter<T>>
        get() = parameters.asSequence()

    override fun getDisplayName(index: Int): String? = parameters.getOrNull(index)?.name
}
