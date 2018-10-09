package com.chrynan.aaaah

interface ItemListUpdater<T : Any> {

    var items: List<T>
}