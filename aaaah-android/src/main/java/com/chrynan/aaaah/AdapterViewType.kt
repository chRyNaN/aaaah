@file:Suppress("unused")

package com.chrynan.aaaah

fun AdapterViewType.from(clazz: Class<*>): ViewType = clazz.hashCode()

typealias ViewType = Int

object AdapterViewType

interface AdapterViewTypesProvider {

    val viewTypes: Map<Class<out AnotherAdapter<*>>, ViewType>
}