package com.chrynan.aaaah

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

object KotlinFileWriter {

    private const val KOTLIN_GENERATED_FILES_LOCATION = "kapt.kotlin.generated"

    fun write(fileSpec: FileSpec, processingEnv: ProcessingEnvironment) {
        fileSpec.writeTo(fileFor(processingEnv = processingEnv))
    }

    private fun fileFor(processingEnv: ProcessingEnvironment) =
            File(processingEnv.options[KOTLIN_GENERATED_FILES_LOCATION])
}