package com.chrynan.aaaah

interface DiffDispatcher<VM : UniqueAdapterItem> {

    suspend fun dispatchDiff(diff: DiffResult<VM>)
}
