package com.chrynan.aaaah

import androidx.recyclerview.widget.DiffUtil

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class DiffProcessor<T : UniqueAdapterItem> : DiffUtil.Callback() {

    var currentList: List<T> = emptyList()
        set(value) {
            lastList = field
            field = value
        }

    private var lastList: List<T> = emptyList()

    override fun getOldListSize(): Int = lastList.size

    override fun getNewListSize(): Int = currentList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = lastList[oldItemPosition].uniqueAdapterId == currentList[newItemPosition].uniqueAdapterId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = lastList[oldItemPosition] == currentList[newItemPosition]

    fun calculateDiff(sortedItems: List<T>): DiffUtil.DiffResult {
        currentList = sortedItems
        return DiffUtil.calculateDiff(this)
    }
}