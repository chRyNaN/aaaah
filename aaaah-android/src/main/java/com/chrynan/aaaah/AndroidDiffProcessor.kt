package com.chrynan.aaaah

import com.chrynan.aaaah.diff.DiffCalculator

class AndroidDiffProcessor<VM : UniqueAdapterItem>(private val diffUtilCalculator: DiffCalculator<VM>) :
    DiffProcessor<VM> {

    override suspend fun processDiff(items: Collection<VM>): AndroidDiffResult<VM> {
        return diffUtilCalculator.calculateDiff(items.toList())
    }
}
