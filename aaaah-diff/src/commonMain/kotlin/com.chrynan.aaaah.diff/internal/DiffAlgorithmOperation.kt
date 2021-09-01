package com.chrynan.aaaah.diff.internal

sealed class DiffAlgorithmOperation<out T> {

    data class Insert<T>(
        val value: T
    ) : DiffAlgorithmOperation<T>()

    data class Update<T>(
        val oldValue: T,
        val newValue: T
    ) : DiffAlgorithmOperation<T>()

    object Delete : DiffAlgorithmOperation<Nothing>()

    object Skip : DiffAlgorithmOperation<Nothing>()
}
