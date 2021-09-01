package com.chrynan.aaaah.diff

internal data class BaseDiffResult<T>(
    override val originalItems: List<T>,
    override val updatedItems: List<T>,
    override val operations: List<DiffOperation<T>>
) : DiffResult<T>
