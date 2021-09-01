@file:Suppress("unused")

package com.chrynan.aaaah.diff

/**
 * A Utility used to determine whether two items from two different lists of the same type are the same. This utility
 * is used when calculating the difference between two lists.
 */
interface DiffCalculator<T> {

    /**
     * Decides whether two objects represent the same Item.
     *
     * For example, if your items have unique ids, this method should check their id equality.
     *
     * @param oldItem The old list item
     * @param newItem The new list item
     * @return True if the two items represent the same object or false if they are different.
     */
    fun areItemsTheSame(oldItem: T, newItem: T): Boolean = areContentsTheSame(oldItem, newItem)

    /**
     * Checks whether two items have the same data. This is used to detect if the contents of an item has changed.
     *
     * This method is called only if [areItemsTheSame] returns `true` for these items.
     *
     * If the items are the same according to the [areItemsTheSame] function, this function further checks whether the
     * contents of the items are the same. For example, if your items have unique ids, and they match then the
     * [areItemsTheSame] should return true, then this function will determine if the contents of the items are the
     * same or not.
     *
     * @param oldItem The old list item
     * @param newItem The new list item
     * @return True if the contents of the items are the same or false if they are different.
     */
    fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}
