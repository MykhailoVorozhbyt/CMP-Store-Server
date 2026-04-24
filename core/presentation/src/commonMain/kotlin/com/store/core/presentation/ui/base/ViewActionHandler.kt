@file:Suppress("unused")

package com.store.core.presentation.ui.base

import com.store.core.presentation.ui.ViewAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update

/**
 * Interface for handling view actions and updating the corresponding view data.
 *
 * @param VA The type of the [ViewAction] being handled.
 * @param VD The type of the view data being updated.
 */
interface ViewActionHandler<VA : ViewAction, VD> {

    interface InPlace<VA : ViewAction, VD> {
        suspend fun handle(action: VA, viewData: VD): VD
    }

    // Do not use it anymore, use Scoped or DefaultScoped instead
    interface StateProduce<VA : ViewAction, VD> {
        suspend fun handle(action: VA, flowData: MutableStateFlow<VD>)
    }

    // Do not use it anymore, use Scoped or DefaultScoped instead
    interface DefaultStateProduce<VA : ViewAction, VD> : InPlace<VA, VD>, StateProduce<VA, VD> {
        override suspend fun handle(action: VA, flowData: MutableStateFlow<VD>) {
            flowData.update { handle(action, flowData.value) }
        }
    }

    interface Scoped<VA : ViewAction, VD> {
        suspend fun handle(action: VA, ctx: ActionHandlerScope<VD>)
    }

    interface DefaultScoped<VA : ViewAction, VD> : InPlace<VA, VD>, Scoped<VA, VD> {
        override suspend fun handle(action: VA, ctx: ActionHandlerScope<VD>) {
            val handledVd = handle(action, ctx.viewData)
            ctx.updateViewData { handledVd }
        }
    }

    interface ScopedResulting<VA : ViewAction, VD, R> {
        suspend fun handle(action: VA, ctx: ActionHandlerScope<VD>): Flow<R>

        // when handler returns some result, but you don't care about it
        suspend fun handleNoResult(action: VA, ctx: ActionHandlerScope<VD>) {
            handle(action, ctx).launchIn(ctx.scope)
        }
    }

}

interface ActionHandlerReadOnlyScope<out VD> : ActionHandlerContext {
    val viewData: VD
}

interface ActionHandlerScope<VD> : ActionHandlerReadOnlyScope<VD> {
    fun updateViewData(update: VD.() -> VD)
}

/**
 * A lens that defines how to read and write a specific part of a larger data structure.
 *
 * @param PVD The type of the parent view data.
 * @param VD The type of the view data being focused on.
 */
open class Lens<PVD, VD>(
    override val read: (PVD) -> VD,
    val write: (PVD, VD) -> PVD
) : ReadOnlyLens<PVD, VD>(read)

open class ReadOnlyLens<PVD, VD>(
    open val read: (PVD) -> VD,
)


// it is okay to create scopes on each handle, they are lightweight,
// don't saved anywhere (I assume you don't save them in collections:) )
// and as a young generation objects will be collected by GC easily and fast
// however, if you can reuse them, do it

fun <PVD, VD> ActionHandlerContext.scopeFor(
    flowData: MutableStateFlow<PVD>,
    lens: Lens<PVD, VD>
): ActionHandlerScope<VD> {
    return ActionHandlerScopeImpl(this, flowData, lens)
}

fun <VD> ActionHandlerContext.scopeFor(
    flowData: MutableStateFlow<VD>,
): ActionHandlerScope<VD> {
    return ActionHandlerScopeNoLensImpl(this, flowData)
}

fun <PVD, VD> ActionHandlerScope<PVD>.childScope(
    lens: Lens<PVD, VD>
): ActionHandlerScope<VD> {
    return ActionHandlerChildScopeImpl(this, lens)
}

fun <PVD, VD> ActionHandlerReadOnlyScope<PVD>.childScope(
    lens: ReadOnlyLens<PVD, VD>
): ActionHandlerReadOnlyScope<VD> {
    return ActionHandlerReadOnlyScopeImpl(this, lens)
}

fun <PVD, VD> ActionHandlerReadOnlyScope<PVD>.childScope(
    read: (PVD) -> VD
): ActionHandlerReadOnlyScope<VD> {
    return ActionHandlerReadOnlyScopeImpl(this, ReadOnlyLens(read))
}

private class ActionHandlerScopeImpl<PVD, VD>(
    private val handlerContext: ActionHandlerContext,
    private val parentStateFlow: MutableStateFlow<PVD>,
    private val lens: Lens<PVD, VD>,
) : ActionHandlerScope<VD>, ActionHandlerContext by handlerContext {

    override val viewData: VD
        get() { // formatting for easier breakpoint setting
            return lens.read(parentStateFlow.value)
        }

    override fun updateViewData(update: VD.() -> VD) {
        parentStateFlow.update { lens.write(it, lens.read(it).update()) }
    }
}

private class ActionHandlerScopeNoLensImpl<VD>(
    handlerContext: ActionHandlerContext,
    private val parentStateFlow: MutableStateFlow<VD>,
) : ActionHandlerScope<VD>, ActionHandlerContext by handlerContext {

    override val viewData: VD
        get() {
            return parentStateFlow.value
        }

    override fun updateViewData(update: VD.() -> VD) {
        parentStateFlow.update { it.update() }
    }
}

private class ActionHandlerChildScopeImpl<PVD, VD>(
    private val parentScope: ActionHandlerScope<PVD>,
    private val lens: Lens<PVD, VD>,
) : ActionHandlerScope<VD>, ActionHandlerContext by parentScope {

    override val viewData: VD
        get() { // formatting for easier breakpoint setting
            return lens.read(parentScope.viewData)
        }

    override fun updateViewData(update: VD.() -> VD) {
        parentScope.updateViewData { lens.write(this, lens.read(this).update()) }
    }
}

private class ActionHandlerReadOnlyScopeImpl<PVD, VD>(
    private val parentScope: ActionHandlerReadOnlyScope<PVD>,
    private val lens: ReadOnlyLens<PVD, VD>,
) : ActionHandlerReadOnlyScope<VD>, ActionHandlerContext by parentScope {

    override val viewData: VD
        get() {
            return lens.read(parentScope.viewData)
        }
}

