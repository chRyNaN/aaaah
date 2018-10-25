package com.chrynan.aaaah

import android.support.v7.util.ListUpdateCallback

interface ItemListUpdater<T : Any> : ListUpdateCallback {

    var items: List<T>
}