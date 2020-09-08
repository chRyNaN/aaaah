package com.chrynan.aaaah.utils

fun String.toConstantFieldName(): String {
    val nameStringBuilder = StringBuilder()

    val chars = toCharArray()

    for (i in chars.indices) {
        val char = chars[i]

        if (Character.isUpperCase(char) && i != 0) {
            nameStringBuilder.append("_$char")
        } else {
            nameStringBuilder.append(Character.toUpperCase(char))
        }
    }

    return nameStringBuilder.toString()
}