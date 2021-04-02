@file:Suppress("unused")

package com.chrynan.aaaah

import kotlinx.coroutines.flow.Flow

interface AdapterItemHandler<VM : UniqueAdapterItem> {

    fun Flow<Collection<VM>>.calculateAndDispatchDiff(): Flow<DiffResult<VM>>
}

fun <VM : UniqueAdapterItem> Flow<Collection<VM>>.calculateAndDispatchDiff(itemHandler: AdapterItemHandler<VM>): Flow<DiffResult<VM>> =
    itemHandler.run {
        calculateAndDispatchDiff()
    }
