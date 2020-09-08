package com.chrynan.aaaah

class AndroidDiffProcessor<VM : UniqueAdapterItem>(private val diffUtilCalculator: DiffUtilCalculator<VM>) :
    DiffProcessor<VM> {

    override suspend fun processDiff(items: Collection<VM>): AndroidDiffResult<VM> {
        return diffUtilCalculator.calculateDiff(items.toList())
    }
}