@file:Suppress("unused")

package com.chrynan.aaaah

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

typealias HandlesItem = (item: Any) -> Boolean
typealias CreateView = (parent: ViewGroup, viewType: ViewType) -> View
typealias BindItem<M> = (view: View, item: M) -> Unit

inline fun <reified M : Any> anotherAdapter(viewType: ViewType,
                                            crossinline onCreateView: CreateView,
                                            crossinline onHandlesItem: HandlesItem,
                                            crossinline onBindItem: BindItem<M>): AnotherAdapter<M> =
        object : AnotherAdapter<M>() {

            override val viewType = viewType

            override fun onHandlesItem(item: Any) = onHandlesItem(item)

            override fun onCreateView(parent: ViewGroup, viewType: ViewType) = onCreateView(parent, viewType)

            override fun onBindItem(view: View, item: M) = onBindItem(view, item)
        }

inline fun <reified M : Any> anotherAdapterManager(vararg adapters: AnotherAdapter<*>) =
        ManagerRecyclerViewAdapter<M>(adapters = adapters.toSet())

inline fun <reified M : Any> anotherAdapter(viewType: ViewType, viewResId: Int, crossinline bind: View.(M) -> Unit) =
        object : AnotherAdapter<M>() {

            override val viewType = viewType

            override fun onHandlesItem(item: Any) = item is M

            override fun onCreateView(parent: ViewGroup, viewType: ViewType): View =
                    LayoutInflater.from(parent.context).inflate(viewResId, parent, false)

            override fun onBindItem(view: View, item: M) = view.bind(item)
        }

inline fun <reified M : Any> RecyclerView.adapter(anotherAdapter: AnotherAdapter<M>) {
    this.adapter = anotherAdapterManager<M>(anotherAdapter)
}