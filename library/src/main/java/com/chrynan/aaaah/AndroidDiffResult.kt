package com.chrynan.aaaah

import android.support.v7.util.DiffUtil

data class AndroidDiffResult<VM : UniqueAdapterItem>(
        override val items: List<VM>,
        val diffUtilResult: DiffUtil.DiffResult
) : DiffResult<VM>