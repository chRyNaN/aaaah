@file:Suppress("unused")

package com.chrynan.aaaah

typealias AdapterId = Long

interface UniqueAdapterItem {

    val uniqueAdapterId: AdapterId
}

inline fun <reified T : Any> T.asUniqueAdapterId() = hashCode().toLong()