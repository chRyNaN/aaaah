package com.chrynan.aaaah.builder

import com.squareup.kotlinpoet.FileSpec

interface KotlinFileSpecBuilder {

    fun build(): FileSpec
}