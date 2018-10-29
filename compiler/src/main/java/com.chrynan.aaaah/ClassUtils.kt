package com.chrynan.aaaah

fun classExists(fullName: String, onDoesNotExist: (() -> Unit)? = null): Boolean =
        try {
            Class.forName(fullName) != null
        } catch (e: ClassNotFoundException) {
            onDoesNotExist?.invoke()
            false
        }