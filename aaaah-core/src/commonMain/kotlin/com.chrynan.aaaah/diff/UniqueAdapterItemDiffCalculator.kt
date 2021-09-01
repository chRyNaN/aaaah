@file:Suppress("unused")

package com.chrynan.aaaah.diff

import com.chrynan.aaaah.UniqueAdapterItem

class UniqueAdapterItemDiffCalculator<VM : UniqueAdapterItem> : DiffCalculator<VM> {

    override fun areItemsTheSame(oldItem: VM, newItem: VM): Boolean = oldItem.uniqueAdapterId == newItem.uniqueAdapterId

    override fun areContentsTheSame(oldItem: VM, newItem: VM): Boolean = oldItem == newItem
}
