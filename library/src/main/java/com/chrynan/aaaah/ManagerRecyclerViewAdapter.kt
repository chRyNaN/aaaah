package com.chrynan.aaaah

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ManagerRecyclerViewAdapter<T : Any>(private val adapters: Set<AnotherAdapter<*>> = emptySet()) : RecyclerView.Adapter<ManagerRecyclerViewAdapter.ViewHolder>() {

    companion object {

        private const val NO_ID = -1L
        private const val INVALID_VIEW_TYPE = -1
    }

    private val items: List<T> = emptyList()

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) =
            items[position].let { if (it is UniqueAdapterItem) it.uniqueAdapterId else NO_ID }

    override fun getItemViewType(position: Int) =
            getAdapterForItem(items[position])?.viewType ?: INVALID_VIEW_TYPE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(view = getAdapterForViewType(viewType)!!.onCreateView(parent, viewType))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        getAdapterForItem(item)?.bindItem(holder.itemView, item)
    }

    private fun getAdapterForItem(item: T) = adapters.firstOrNull { it.handlesItem(item) }

    private fun getAdapterForViewType(viewType: ViewType) = adapters.firstOrNull { it.viewType == viewType }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}