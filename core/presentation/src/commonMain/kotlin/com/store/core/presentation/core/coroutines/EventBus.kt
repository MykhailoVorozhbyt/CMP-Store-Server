package com.store.core.presentation.core.coroutines

import com.store.core.utils.Logger
import com.store.core.utils.e
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.withTimeout


/**
 * Lightweight in-memory event bus that decouples producers and consumers
 * **without holding references to heavy objects** (e.g. `ViewModel`, `CoroutineScope`).
 *
 * Two flavours are provided:
 * * **[SingleConsumer]** – one subscriber, **loss-free** delivery via [Channel].
 * * **[MultiConsumer]**  – many subscribers, back-pressured via [MutableSharedFlow].
 *
 * Implement your feature against [Producer] and/or [Consumer] and inject the
 * concrete implementation that fits the load profile.
 */
interface EventBus {

    /** Read-only side of the bus. */
    interface Consumer<out E> : EventBus {
        val events: Flow<E>
    }

    /** Write-only side of the bus. */
    interface Producer<in E> : EventBus {
        fun emit(event: E): Boolean
    }

    interface Full<E> : Consumer<E>, Producer<E>

    /**
     * **Loss-free, single-subscriber bus.**
     *
     * * Uses an unbounded [Channel] — **no drops** even under burst traffic.
     * * Exactly **one** consumer may collect [events]; **other trying to collect will fail with exception**.
     *
     * Suitable for internal component wiring where only one listener exists.
     */
    open class SingleConsumer<E> : Full<E> {

        private val eventsChannel: Channel<E> = Channel(Channel.UNLIMITED)
        override val events: Flow<E> = eventsChannel.consumeAsFlow()

        override fun emit(event: E): Boolean {
            return eventsChannel.trySend(event).isSuccess
        }
    }

    /**
     * **Multi-subscriber bus** backed by [MutableSharedFlow].
     *
     * * `buffer` – extra slots (default 128).
     *   Adequate even for **50–100 events per second**, as long as the producer
     *   and consumer are not on the same thread and the producer isn't looping aggressively.
     * * Increase the buffer for heavy streams or set a different
     *   [BufferOverflow] strategy.
     *
     * Occasional drops may occur only when producers overrun the buffer
     * faster than consumers can drain it.
     */
    open class MultiConsumer<E>(
        buffer: Int = 128, overflow: BufferOverflow = BufferOverflow.DROP_OLDEST
    ) : Full<E> {

        private val _events = MutableSharedFlow<E>(
            replay = 0, extraBufferCapacity = buffer, onBufferOverflow = overflow
        )

        final override val events: SharedFlow<E> = _events.asSharedFlow()

        final override fun emit(event: E): Boolean {
            return _events.tryEmit(event)
        }
    }
}

/**
 * Event bus that blocks the producer until the consumer **fully processes**
 * the event, not just receives.
 * Useful for “critical last message” situations (exit etc.).
 *
 * @see Base – default implementation.
 */
interface SuspendEventBus : EventBus {

    interface Consumer<out E> : SuspendEventBus, EventBus.Consumer<E> {
        suspend fun consume(block: suspend (E) -> Unit)
    }

    interface Producer<in E> : SuspendEventBus, EventBus.Producer<E> {
        /**
         * Sends an event and suspends until the consumer **completes** the work
         * or the [timeout] (ms) elapses.
         *
         * A timeout safeguards against a dead or hung consumer.
         * Pick a value appropriate for your processing time budget.
         */
        suspend fun emitSuspending(event: E, timeout: Long = 3000)
    }

    interface Full<E> : Consumer<E>, Producer<E>, EventBus.Full<E>

    /**
     * Rendezvous-channel implementation that pairs each event with a
     * [CompletableDeferred] acknowledgement.
     * Implements both [SuspendEventBus.Full] and [EventBus.Full] extending [EventBus.SingleConsumer]
     *
     * *Producer path*
     * `emitBlocking` sends the event + completion token and awaits the token
     * completion or exits if `timeout` is reached.
     *
     * *Consumer path*
     * `consume` receives the wrapper, executes the user block, then completes
     * the token to release the producer.
     */
    open class Base<E> : Full<E>, EventBus.SingleConsumer<E>() {

        private data class CompletionWrap<E>(
            val event: E, val completion: CompletableDeferred<Unit>
        )

        private val channel: Channel<CompletionWrap<E>> = Channel(Channel.RENDEZVOUS)

        override suspend fun consume(block: suspend (E) -> Unit) {
            channel.consumeEach { wrap ->
                block(wrap.event)
                wrap.completion.complete(Unit)
            }
        }

        override suspend fun emitSuspending(event: E, timeout: Long) {
            val completion = CompletableDeferred<Unit>()
            val wrap = CompletionWrap(event, completion)
            try {
                withTimeout(timeout) {
                    channel.send(wrap)
                    completion.await()
                }
            } catch (e: TimeoutCancellationException) {
                Logger.e(
                    "Exception: $e\nConsumer didn't processed an event in ${timeout}ms or there is no consumer. Event: $event"
                )
            }
        }
    }

}

