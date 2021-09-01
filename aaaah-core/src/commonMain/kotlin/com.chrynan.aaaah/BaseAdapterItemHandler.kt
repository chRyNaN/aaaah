@file:Suppress("unused")

package com.chrynan.aaaah

import com.chrynan.aaaah.diff.DiffResult
import com.chrynan.dispatchers.dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class BaseAdapterItemHandler<VM : UniqueAdapterItem>(
    private val diffProcessor: DiffProcessor<VM>,
    private val diffDispatcher: DiffDispatcher<VM>,
    private val processDispatcher: CoroutineDispatcher = dispatchers.io,
    private val uiDispatcher: CoroutineDispatcher = dispatchers.main
) : AdapterItemHandler<VM> {

    @ExperimentalCoroutinesApi
    override fun Flow<Collection<VM>>.calculateAndDispatchDiff(): Flow<DiffResult<VM>> =
        map(diffProcessor::processDiff)
            .flowOn(processDispatcher)
            .onEach { diffDispatcher.dispatchDiff(it) }
            .flowOn(uiDispatcher)
}

@Suppress("FunctionName")
fun <VM : UniqueAdapterItem> AdapterItemHandler(
    diffProcessor: DiffProcessor<VM>,
    diffDispatcher: DiffDispatcher<VM>,
    processDispatcher: CoroutineDispatcher = dispatchers.io,
    uiDispatcher: CoroutineDispatcher = dispatchers.main
): AdapterItemHandler<VM> =
    BaseAdapterItemHandler(
        diffProcessor = diffProcessor,
        diffDispatcher = diffDispatcher,
        processDispatcher = processDispatcher,
        uiDispatcher = uiDispatcher
    )