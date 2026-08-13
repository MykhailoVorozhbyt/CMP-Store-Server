package com.store.core.presentation.coroutines

import com.store.core.presentation.coroutines.SingleJobLaunch.Companion.DEFAULT_JOB_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Base helper that guarantees **exactly one** coroutine launched by this instance
 * is running at any moment.
 *
 * The first subsequent call to [launchSingle] _cancels_ any existing job
 * (if still active) and immediately starts a new one, so only the latest
 * request is honoured.
 * Use it to serialise work that must not overlap – e.g. network fetches,
 * disk writes, analytics uploads – where "last call wins" semantics are desired.
 */
@OptIn(InternalCoroutinesApi::class)
abstract class SingleJobLaunch : SynchronizedObject() {

    private val jobs: MutableMap<String, Job> = mutableMapOf()

    /**
     * Launches a new coroutine, cancelling any previously running job with the same [jobKey].
     *
     * The default [jobKey] is [DEFAULT_JOB_KEY], so if you don't provide a key,
     * all calls to this method will cancel the previous job.
     * To manage multiple independent jobs, provide distinct keys.
     *
     * Thread-safe for concurrent invocations across threads or coroutines.
     *
     * @param jobKey Key to identify the job. Defaults to [DEFAULT_JOB_KEY].
     * @return The newly launched [Job].
     */
    protected fun CoroutineScope.launchSingle(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        jobKey: String = DEFAULT_JOB_KEY,
        block: suspend CoroutineScope.() -> Unit
    ): Job = synchronized(this@SingleJobLaunch) {
        jobs[jobKey]?.cancel()
        launch(context, start, block).also { jobs[jobKey] = it }
    }

    /**
     * Cancels active jobs.
     *
     * If [jobKey] is provided, only the job with that key is cancelled.
     * Otherwise, the [policy] determines which jobs to cancel:
     * - [CancelPolicy.All]: cancels all active jobs.
     * - [CancelPolicy.OnlyDefault]: cancels only the default job [DEFAULT_JOB_KEY].
     *
     * @param jobKey Optional key of the job to cancel. If null, uses [policy].
     * @param policy Policy to determine which jobs to cancel if [jobKey] is null.
     */
    fun cancel(
        jobKey: String? = null,
        policy: CancelPolicy = CancelPolicy.All,
    ) = synchronized(this) {
        if (jobKey != null) {
            jobs[jobKey]?.cancel()
            return@synchronized
        }
        when (policy) {
            CancelPolicy.All -> {
                jobs.values.forEach { it.cancel() }
                jobs.clear()
            }

            CancelPolicy.OnlyDefault -> jobs[DEFAULT_JOB_KEY]?.cancel()
        }
    }

    class Delegate(private val scope: CoroutineScope) : SingleLauncher, SingleJobLaunch() {

        override fun launchSingle(
            context: CoroutineContext,
            jobKey: String,
            start: CoroutineStart,
            block: suspend CoroutineScope.() -> Unit
        ): Job = scope.launchSingle(
            context = context,
            start = start,
            jobKey = jobKey,
            block = block
        )

        override fun cancelSingle(jobKey: String) {
            cancel(jobKey)
        }
    }

    enum class CancelPolicy {
        All,
        OnlyDefault
    }

    companion object {
        const val DEFAULT_JOB_KEY = "default"
    }
}