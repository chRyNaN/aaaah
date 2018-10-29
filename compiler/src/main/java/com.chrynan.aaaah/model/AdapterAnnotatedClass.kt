package com.chrynan.aaaah.model

import com.chrynan.aaaah.utils.toConstantFieldName

data class AdapterAnnotatedClass(
        val packageName: String,
        val simpleName: String,
        val providedName: String
) {

    val fullNameWithClassSuffix: String
        get() = "$fullName.class"

    val fieldName: String
        get() = if (providedName.isBlank()) simpleName.toConstantFieldName() else providedName

    private val fullName: String
        get() = "$packageName.$simpleName"
}