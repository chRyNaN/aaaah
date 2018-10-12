package com.chrynan.aaaah

import android.view.View
import android.view.ViewGroup

typealias ViewType = Int

abstract class AnotherAdapter<M : Any> {

    abstract val viewType: ViewType

    abstract fun onHandlesItem(item: Any): Boolean

    abstract fun onCreateView(parent: ViewGroup, viewType: ViewType): View

    abstract fun onBindItem(view: View, item: M)

    internal fun bindItem(view: View, item: Any) {
        castOrNull(item)?.let { onBindItem(view, it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun castOrNull(item: Any): M? =
            try {
                item as? M
            } catch (e: Exception) {
                null
            }
}