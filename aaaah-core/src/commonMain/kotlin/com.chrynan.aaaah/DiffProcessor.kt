package com.chrynan.aaaah

import com.chrynan.aaaah.diff.DiffResult

interface DiffProcessor<VM : UniqueAdapterItem> {

    suspend fun processDiff(items: Collection<VM>): DiffResult<VM>
}
