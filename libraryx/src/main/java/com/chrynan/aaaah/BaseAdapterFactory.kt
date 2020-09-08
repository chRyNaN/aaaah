package com.chrynan.aaaah

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapterFactory<VM : UniqueAdapterItem> : AdapterFactory<VM> {

    abstract val adapters: Set<AnotherAdapter<*>>

    @Suppress("RemoveExplicitTypeArguments") // For some reason the build fails without the explicit type parameter
    override val decorators: List<RecyclerView.ItemDecoration> by lazy { emptyList<RecyclerView.ItemDecoration>() }

    @Suppress("RemoveExplicitTypeArguments") // For some reason the build fails without the explicit type parameter
    override val diffUtilCalculator: DiffUtilCalculator<VM> by lazy { DiffUtilCalculator<VM>() }

    override val diffProcessor: DiffProcessor<VM> by lazy {
        AndroidDiffProcessor(
            diffUtilCalculator
        )
    }

    override val diffDispatcher: DiffDispatcher<VM> by lazy { AndroidDiffDispatcher(adapter) }

    override val adapter: ManagerRecyclerViewAdapter<VM> by lazy {
        ManagerRecyclerViewAdapter<VM>(adapters = adapters)
    }
}