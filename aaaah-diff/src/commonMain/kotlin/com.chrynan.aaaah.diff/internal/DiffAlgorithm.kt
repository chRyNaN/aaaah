package com.chrynan.aaaah.diff.internal

import com.chrynan.aaaah.diff.DiffCalculator

interface DiffAlgorithm<T> {

    fun generateDiff(
        original: List<T>,
        updated: List<T>,
        diffCalculator: DiffCalculator<T>
    ): Sequence<DiffAlgorithmOperation<T>>
}
