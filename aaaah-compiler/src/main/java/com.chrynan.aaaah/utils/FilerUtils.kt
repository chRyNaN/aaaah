package com.chrynan.aaaah.utils

import com.squareup.javapoet.JavaFile
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

fun Filer.createJavaFile(file: JavaFile, fileName: String, messager: Messager) {
    try {
        file.writeTo(this)
    } catch (e: Exception) {
        messager.printMessage(Diagnostic.Kind.ERROR, "Error creating $fileName class.\n" +
                "LocalizedMessage = ${e.localizedMessage}\n" +
                "Exception = $e")
    }
}