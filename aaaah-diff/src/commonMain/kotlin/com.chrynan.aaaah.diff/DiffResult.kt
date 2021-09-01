package com.chrynan.aaaah.diff

interface DiffResult<T> {

    val originalItems: List<T>

    val updatedItems: List<T>

    val operations: List<DiffOperation<T>>
}

@Suppress("FunctionName")
fun <T> DiffResult(
    originalItems: List<T>,
    updatedItems: List<T>,
    operations: List<DiffOperation<T>>
): DiffResult<T> = BaseDiffResult(
    originalItems = originalItems,
    updatedItems = updatedItems,
    operations = operations
)
