@file:Suppress("unused")

package com.chrynan.aaaah

import kotlin.reflect.KClass

typealias ViewType = Int

object AdapterViewType

inline fun <reified T : Any> AdapterViewType.from(clazz: KClass<T>): ViewType = clazz.hashCode()

interface AdapterViewTypesProvider {

    val viewTypes: Map<AnotherAdapter<*>, ViewType>
}