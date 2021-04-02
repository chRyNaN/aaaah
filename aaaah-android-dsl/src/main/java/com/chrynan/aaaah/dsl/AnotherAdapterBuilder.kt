@file:Suppress("unused")

package com.chrynan.aaaah.dsl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrynan.aaaah.*
import kotlin.properties.Delegates

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

    override fun onCreateView(parent: ViewGroup, inflater: LayoutInflater, viewType: ViewType) = createView(parent, inflater, viewType)

    override fun View.onBindItem(item: M, position: Int) = bindItem(item, position)
}

fun <M : Any> anotherAdapter(block: AnotherAdapterBuilder<M>.() -> Unit): AnotherAdapter<M> {
    val builder = AnotherAdapterBuilder<M>()
    block(builder)
    return builder.build()
}