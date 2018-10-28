@file:Suppress("unused")

package com.chrynan.aaaah

typealias ViewType = Int

object AdapterViewType

inline fun <reified T : Any> AdapterViewType.from(clazz: Class<T>): ViewType = clazz.hashCode()

interface AdapterViewTypesProvider {

    val viewTypes: Map<Class<AnotherAdapter<*>>, ViewType>
}