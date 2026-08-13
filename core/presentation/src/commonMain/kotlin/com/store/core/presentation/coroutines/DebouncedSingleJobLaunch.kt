package com.store.core.presentation.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Extends [SingleJobLaunch] with debounced coroutine launching.
 *
 * Ensures that **only one** debounced [Job] is active at a time.
 * When called repeatedly within the debounce interval, previous jobs are cancelled
 * and only the latest action is executed after the specified delay.
 *
 * Thread-safe for concurrent invocations across threads or coroutines.
 * Intended for use cases such as text input debouncing or scenarios where rapid
 * repeated triggers **should collapse into a single execution**.
 *
 * @see SingleJobLaunch
 */
abstract class DebouncedSingleJobLaunch : SingleJobLaunch() {

    /**
     * Launches a debounced coroutine, cancelling any previously pending debounced job
     * **with the same [jobKey]**.
     *
     * Pass distinct keys when one instance debounces several independent things — on the
     * default key they collapse into one another, which is rarely what a shared component wants.
     *
     * @param debounce Delay interval in milliseconds before executing [action].
     * @param jobKey Key isolating this debounce from unrelated ones. Defaults to the shared key.
     * @param action Asynchronous action to execute after the debounce period.
     */
    protected fun CoroutineScope.launchDebounced(
        debounce: Long,
        jobKey: String = DEFAULT_JOB_KEY,
        action: suspend () -> Unit
    ) {
        launchSingle(jobKey = jobKey) {
            delay(debounce.milliseconds)
            action()
        }
    }
}
