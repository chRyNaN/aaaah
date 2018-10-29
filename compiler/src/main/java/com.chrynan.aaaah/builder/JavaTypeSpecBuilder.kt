package com.chrynan.aaaah.builder

import com.squareup.javapoet.TypeSpec

interface JavaTypeSpecBuilder {

    fun build(): TypeSpec
}