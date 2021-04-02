package com.chrynan.aaaah

interface DiffProcessor<VM : UniqueAdapterItem> {

    suspend fun processDiff(items: Collection<VM>): DiffResult<VM>
}
