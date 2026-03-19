package com.store.core.presentation.ui.base

import kotlinx.coroutines.flow.MutableStateFlow


interface ViewDataInitializer {
    interface InPlace<VD> {
        suspend fun initialize(provided: VD? = null): VD
    }

    interface StateProduce<VD> {
        fun initialize(viewDataState: MutableStateFlow<VD>)
    }

    interface Scoped<VD> {
        fun initialize(ctx: ActionHandlerScope<VD>)
    }

}
