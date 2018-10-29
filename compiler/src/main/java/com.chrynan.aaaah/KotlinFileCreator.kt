package com.chrynan.aaaah

import java.io.File
import javax.annotation.processing.ProcessingEnvironment

object KotlinFileCreator {

    private const val KOTLIN_GENERATED_FILES_LOCATION = "kapt.kotlin.generated"

    fun fileFor(processingEnv: ProcessingEnvironment, fullClassName: String) =
            File(processingEnv.options[KOTLIN_GENERATED_FILES_LOCATION], fullClassName)
}