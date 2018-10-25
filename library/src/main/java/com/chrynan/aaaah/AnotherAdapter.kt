package com.chrynan.aaaah

import android.view.View
import android.view.ViewGroup

abstract class AnotherAdapter<M : Any> {

    abstract val viewType: ViewType

    abstract fun onCreateView(parent: ViewGroup, viewType: ViewType): View

    abstract fun onBindItem(view: View, item: M)

    open fun onHandlesItem(item: M): Boolean = true

    internal fun handlesItem(item: Any): Boolean =
            castOrNull(item)?.let { onHandlesItem(it) } ?: false

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