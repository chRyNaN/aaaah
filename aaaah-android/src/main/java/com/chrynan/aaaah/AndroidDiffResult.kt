package com.chrynan.aaaah

import androidx.recyclerview.widget.DiffUtil
import com.chrynan.aaaah.diff.DiffResult

data class AndroidDiffResult<VM : UniqueAdapterItem>(
    override val updatedItems: List<VM>,
    val diffUtilResult: DiffUtil.DiffResult
) : DiffResult<VM>