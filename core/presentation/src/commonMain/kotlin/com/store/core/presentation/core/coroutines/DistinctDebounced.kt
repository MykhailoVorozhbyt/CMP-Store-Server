package com.store.core.presentation.core.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlin.jvm.JvmField
import kotlin.reflect.KClass
import kotlin.time.Clock


/**
 * Debounced version of [distinctUntilChanged] that allows you to specify a debounce time.
 * Ignores same values that are emitted within the specified debounce time.
 *
 * @param timeProvided need for testing purposes, to provide a custom time source such testScheduler.
 * @see [distinctUntilChanged]
 */
@Suppress("UNCHECKED_CAST")
fun <T> Flow<T>.distinctUntilChangedDebounced(
    debounceTimeMillis: Long,
    timeProvided: () -> Long = { Clock.System.now().epochSeconds },
    areEquivalent: (old: T, new: T) -> Boolean = defaultAreEquivalent
): Flow<T> = when {
    debounceTimeMillis < 1 -> distinctUntilChanged(areEquivalent)
    this is StateFlow<*> -> this
    this is DistinctFlowImpl<*> && this.areEquivalent === areEquivalent -> this
    else -> DistinctFlowImpl(
        upstream = this,
        keySelector = defaultKeySelector,
        areEquivalent = areEquivalent as (Any?, Any?) -> Boolean,
        debounceTimeMillis = debounceTimeMillis,
        timeProvided = timeProvided
    )
}

/**
 * Debounced version of [distinctUntilChangedBy] that allows you to specify a debounce time.
 * Ignores same values that are emitted within the specified debounce time.
 *
 * @see [distinctUntilChangedBy]
 */
fun <T> Flow<T>.distinctUntilChangedDebouncedBy(
    debounceTimeMillis: Long,
    timeProvided: () -> Long = { Clock.System.now().epochSeconds },
    keySelector: (T) -> Any? = defaultKeySelector
): Flow<T> = when {
    debounceTimeMillis < 1 -> distinctUntilChangedBy(keySelector)
    this is StateFlow<*> -> this
    this is DistinctFlowImpl<*> && this.keySelector === keySelector -> this
    else -> DistinctFlowImpl(
        upstream = this,
        keySelector = keySelector,
        areEquivalent = defaultAreEquivalent,
        debounceTimeMillis = debounceTimeMillis,
        timeProvided = timeProvided
    )
}

/**
 * Debounced version of [distinctUntilChanged] that allows you to specify a debounce time and a class type.
 * Ignores same values that are emitted within the specified debounce time and **are not of the specified type**.
 *
 * @param type the type to filter by, only values of this type will be filtering. **All other emits as usual**.
 * @param timeProvided need for testing purposes, to provide a custom time source such testScheduler.
 * @see [distinctUntilChanged]
 */
fun <T> Flow<T>.distinctUntilChangedDebouncedByType(
    debounceTimeMillis: Long,
    type: KClass<*>,
    timeProvided: () -> Long = { Clock.System.now().epochSeconds }
): Flow<T> = when {
    debounceTimeMillis < 1 -> distinctUntilChanged()
    this is StateFlow<*> -> this
    this is DistinctFlowImpl<*> && this.type === type -> this
    else -> DistinctFlowImpl(
        upstream = this,
        keySelector = defaultKeySelector,
        areEquivalent = defaultAreEquivalent,
        debounceTimeMillis = debounceTimeMillis,
        type = type,
        timeProvided = timeProvided
    )
}

private const val NULL = "<NULL>"

private val defaultKeySelector: (Any?) -> Any? = { it }

private val defaultAreEquivalent: (Any?, Any?) -> Boolean = { old, new -> old == new }

private class DistinctFlowImpl<T>(
    private val upstream: Flow<T>,
    @JvmField val keySelector: (T) -> Any?,
    @JvmField val areEquivalent: (old: Any?, new: Any?) -> Boolean,
    @JvmField val debounceTimeMillis: Long,
    @JvmField val type: KClass<*>? = null,
    @JvmField val timeProvided: () -> Long = { Clock.System.now().epochSeconds }
) : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        var previousKey: Any? = NULL
        var lastTime = 0L

        upstream.collect inner@{ value ->
            if (type != null && !type.isInstance(value)) {
                collector.emit(value)
                return@inner
            }
            val key = keySelector(value)
            val now = timeProvided()
            val outOfDebounceWindow = now - lastTime >= debounceTimeMillis

            if (previousKey === NULL || !areEquivalent(previousKey, key) || outOfDebounceWindow) {
                previousKey = key
                lastTime = now

                collector.emit(value)
            }
        }
    }
}
