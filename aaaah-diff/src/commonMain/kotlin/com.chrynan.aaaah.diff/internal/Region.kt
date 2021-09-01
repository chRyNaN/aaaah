package com.chrynan.aaaah.diff.internal

internal data class Region(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

internal val Region.width: Int
    get() = right - left

internal val Region.height: Int
    get() = bottom - top

internal val Region.size: Int
    get() = width + height

internal val Region.delta: Int
    get() = width - height
