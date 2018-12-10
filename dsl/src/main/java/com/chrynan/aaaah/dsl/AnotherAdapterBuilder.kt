@file:Suppress("unused")

package com.chrynan.aaaah.dsl

import android.view.View
import android.view.ViewGroup
import com.chrynan.aaaah.AnotherAdapter
import com.chrynan.aaaah.ViewType
import kotlin.properties.Delegates

typealias HandlesItem = (item: Any) -> Boolean
typealias CreateView = (parent: ViewGroup, viewType: ViewType) -> View
typealias BindItem<M> = (view: View, item: M) -> Unit

class AnotherAdapterBuilder<M : Any> internal constructor() {

    var viewType by Delegates.notNull<ViewType>()

    private var handlesItem by Delegates.notNull<HandlesItem>()
    private var createView by Delegates.notNull<CreateView>()
    private var bindItem by Delegates.notNull<BindItem<M>>()

    fun handlesItem(block: HandlesItem) {
        handlesItem = block
    }

    fun createView(block: CreateView) {
        createView = block
    }

    fun bindItem(block: BindItem<M>) {
        bindItem = block
    }

    internal fun build(): AnotherAdapter<M> {
        checkNotNull(viewType) { "Must provide a ViewType in the anotherAdapter DSL function block." }
        checkNotNull(handlesItem) { "Must provide a handlesItem function in the anotherAdapter DSL function block." }
        checkNotNull(createView) { "Must provide a createView function in the anotherAdapter DSL function block." }
        checkNotNull(bindItem) { "Must provide a bindItem function in the anotherAdapter DSL function block." }

        return ParameterProvidedAnotherAdapter(
                viewType = viewType,
                handlesItem = handlesItem,
                createView = createView,
                bindItem = bindItem)
    }
}

internal class ParameterProvidedAnotherAdapter<M : Any>(
        override val viewType: ViewType,
        private val handlesItem: HandlesItem,
        private val createView: CreateView,
        private val bindItem: BindItem<M>
) : AnotherAdapter<M>() {

    override fun onHandlesItem(item: Any) = handlesItem(item)

    override fun onCreateView(parent: ViewGroup, viewType: ViewType) = createView(parent, viewType)

    override fun onBindItem(view: View, item: M) = bindItem(view, item)
}

fun <M : Any> anotherAdapter(block: AnotherAdapterBuilder<M>.() -> Unit): AnotherAdapter<M> {
    val builder = AnotherAdapterBuilder<M>()
    block(builder)
    return builder.build()
}