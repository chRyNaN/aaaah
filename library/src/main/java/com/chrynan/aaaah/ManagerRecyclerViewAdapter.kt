package com.chrynan.aaaah

import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class ManagerRecyclerViewAdapter<T : Any>(private val adapters: Set<AnotherAdapter<*>> = emptySet()) : RecyclerView.Adapter<ManagerRecyclerViewAdapter.ViewHolder>(),
        ListUpdateCallback,
        ItemListUpdater<T> {

    companion object {

        private const val NO_ID = -1L
        private const val INVALID_VIEW_TYPE = -1
    }

    override var items: List<T> = emptyList()

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

    override fun onChanged(position: Int, count: Int, payload: Any?) = notifyItemRangeChanged(position, count, payload)

    override fun onMoved(fromPosition: Int, toPosition: Int) = notifyItemMoved(fromPosition, toPosition)

    override fun onInserted(position: Int, count: Int) = notifyItemRangeInserted(position, count)

    override fun onRemoved(position: Int, count: Int) = notifyItemRangeRemoved(position, count)

    private fun getAdapterForItem(item: T) = adapters.firstOrNull { it.onHandlesItem(item) }

    private fun getAdapterForViewType(viewType: ViewType) = adapters.firstOrNull { it.viewType == viewType }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}