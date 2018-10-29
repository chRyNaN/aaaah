package com.chrynan.aaaah

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

object KotlinFileWriter {

    private const val KOTLIN_GENERATED_FILES_LOCATION = "kapt.kotlin.generated"

    fun write(fileSpec: FileSpec, processingEnv: ProcessingEnvironment, fullClassName: String) {
        fileSpec.writeTo(fileFor(processingEnv = processingEnv, fullClassName = fullClassName))
    }

    private fun fileFor(processingEnv: ProcessingEnvironment, fullClassName: String) =
            File(processingEnv.options[KOTLIN_GENERATED_FILES_LOCATION], fullClassName)
}