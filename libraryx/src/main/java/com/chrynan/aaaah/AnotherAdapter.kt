package com.chrynan.aaaah

import android.view.View
import android.view.ViewGroup

abstract class AnotherAdapter<M : Any> {

    abstract val viewType: ViewType

    // Don't try to make this take a generic parameter, it doesn't work
    abstract fun onHandlesItem(item: Any): Boolean

    abstract fun onCreateView(parent: ViewGroup, viewType: ViewType): View

    abstract fun View.onBindItem(item: M)

    internal fun bindItem(view: View, item: Any) {
        castOrNull(item)?.let { view.onBindItem(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun castOrNull(item: Any): M? =
            try {
                item as? M
            } catch (e: Exception) {
                null
            }
}