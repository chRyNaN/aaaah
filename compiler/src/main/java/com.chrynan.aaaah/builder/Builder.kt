package com.chrynan.aaaah.builder

import com.squareup.javapoet.TypeSpec

interface Builder {

    fun build(): TypeSpec
}