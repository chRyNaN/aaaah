@file:Suppress("unused")

package com.chrynan.aaaah

import com.chrynan.aaaah.diff.DiffResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AdapterItemHandler<VM : UniqueAdapterItem> {

    fun Flow<Collection<VM>>.calculateAndDispatchDiff(): Flow<DiffResult<VM>>
}

fun <VM : UniqueAdapterItem> AdapterItemHandler<VM>.calculateAndDispatchDiff(items: Collection<VM>): Flow<DiffResult<VM>> =
    flow { emit(items) }.calculateAndDispatchDiff()

fun <VM : UniqueAdapterItem> AdapterItemHandler<VM>.calculateAndDispatchDiff(flow: Flow<Collection<VM>>): Flow<DiffResult<VM>> =
    flow.calculateAndDispatchDiff()

fun <VM : UniqueAdapterItem> Flow<Collection<VM>>.calculateAndDispatchDiff(itemHandler: AdapterItemHandler<VM>): Flow<DiffResult<VM>> =
    itemHandler.run {
        calculateAndDispatchDiff()
    }

fun <VM : UniqueAdapterItem> Collection<VM>.calculateAndDispatchDiff(itemHandler: AdapterItemHandler<VM>): Flow<DiffResult<VM>> =
    flow { emit(this@calculateAndDispatchDiff) }.calculateAndDispatchDiff(itemHandler = itemHandler)
