package com.chrynan.aaaah

import com.chrynan.aaaah.diff.DiffResult

interface DiffDispatcher<VM : UniqueAdapterItem> {

    suspend fun dispatchDiff(diff: DiffResult<VM>)
}
