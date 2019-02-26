package com.chrynan.aaaah

import androidx.recyclerview.widget.ListUpdateCallback

interface ItemListUpdater<T : Any> : ListUpdateCallback {

    var items: List<T>
}