@file:Suppress("unused")

package com.chrynan.aaaah

typealias ViewType = Int

object AdapterViewType

fun AdapterViewType.from(clazz: Class<*>): ViewType = clazz.hashCode()

interface AdapterViewTypesProvider {

    val viewTypes: Map<Class<out AnotherAdapter<*>>, ViewType>
}