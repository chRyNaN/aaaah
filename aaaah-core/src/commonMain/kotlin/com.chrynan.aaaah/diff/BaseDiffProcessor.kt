package com.chrynan.aaaah.diff

import com.chrynan.aaaah.DiffProcessor
import com.chrynan.aaaah.UniqueAdapterItem

class BaseDiffProcessor<VM : UniqueAdapterItem> : DiffProcessor<VM> {

    override suspend fun processDiff(items: Collection<VM>): DiffResult<VM> {
        TODO()
    }
}
