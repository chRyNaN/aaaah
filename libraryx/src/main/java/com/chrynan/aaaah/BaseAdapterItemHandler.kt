package com.chrynan.aaaah

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BaseAdapterItemHandler<VM : UniqueAdapterItem>(
    private val diffProcessor: DiffProcessor<VM>,
    private val diffDispatcher: DiffDispatcher<VM>,
    private val processDispatcher: CoroutineDispatcher,
    private val uiDispatcher: CoroutineDispatcher
) : AdapterItemHandler<VM> {

    @ExperimentalCoroutinesApi
    override fun Flow<Collection<VM>>.calculateAndDispatchDiff(): Flow<DiffResult<VM>> =
        map(diffProcessor::processDiff)
            .flowOn(processDispatcher)
            .onEach { diffDispatcher.dispatchDiff(it) }
            .flowOn(uiDispatcher)
}