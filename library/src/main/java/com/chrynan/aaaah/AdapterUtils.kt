@file:Suppress("unused")

package com.chrynan.aaaah

import android.view.View
import android.view.ViewGroup

inline fun <reified M : Any> anotherAdapter(viewType: ViewType,
                                            crossinline onCreateView: (parent: ViewGroup, viewType: ViewType) -> View,
                                            crossinline onHandlesItem: (item: Any) -> Boolean,
                                            crossinline onBindItem: (view: View, item: M) -> Unit): AnotherAdapter<M> =
        object : AnotherAdapter<M>() {

            override val viewType = viewType

            override fun onHandlesItem(item: Any) = onHandlesItem(item)

            override fun onCreateView(parent: ViewGroup, viewType: ViewType) = onCreateView(parent, viewType)

            override fun onBindItem(view: View, item: M) = onBindItem(view, item)
        }

inline fun <reified M : Any> anotherAdapterManager(vararg adapters: AnotherAdapter<*>) =
        ManagerRecyclerViewAdapter<M>(adapters = adapters.toSet())