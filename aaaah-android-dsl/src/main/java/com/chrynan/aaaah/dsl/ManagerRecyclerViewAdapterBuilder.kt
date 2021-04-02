@file:Suppress("unused")

package com.chrynan.aaaah.dsl

import com.chrynan.aaaah.AnotherAdapter
import com.chrynan.aaaah.ManagerRecyclerViewAdapter

class ManagerRecyclerViewAdapterBuilder<T : Any> internal constructor() {

    private val adapters = mutableSetOf<AnotherAdapter<*>>()
    private var generatedViewType = 0

    fun <M : T> anotherAdapter(block: AnotherAdapterBuilder<M>.() -> Unit) {
        val builder = AnotherAdapterBuilder<M>()
        block(builder)
        adapters.add(builder.build())
    }

    fun <M : T> anotherAdapter(adapter: AnotherAdapter<M>) {
        adapters.add(adapter)
    }

    fun <M : T> anotherAdapterWithGeneratedViewType(block: AnotherAdapterBuilder<M>.() -> Unit) {
        val builder = AnotherAdapterBuilder<M>()
        builder.viewType = generatedViewType
        generatedViewType++
        block(builder)
        adapters.add(builder.build())
    }

    internal fun build(): ManagerRecyclerViewAdapter<T> = ManagerRecyclerViewAdapter(adapters = adapters)
}

fun <T : Any> adapters(block: ManagerRecyclerViewAdapterBuilder<T>.() -> Unit): ManagerRecyclerViewAdapter<T> {
    val builder = ManagerRecyclerViewAdapterBuilder<T>()
    block(builder)
    return builder.build()
}

fun <T : Any> anotherAdapterManager(block: ManagerRecyclerViewAdapterBuilder<T>.() -> Unit): ManagerRecyclerViewAdapter<T> {
    val builder = ManagerRecyclerViewAdapterBuilder<T>()
    block(builder)
    return builder.build()
}