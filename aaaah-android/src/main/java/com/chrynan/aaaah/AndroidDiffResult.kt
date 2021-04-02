package com.chrynan.aaaah

import androidx.recyclerview.widget.DiffUtil

data class AndroidDiffResult<VM : UniqueAdapterItem>(
        override val items: List<VM>,
        val diffUtilResult: DiffUtil.DiffResult
) : DiffResult<VM>