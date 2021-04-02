package com.chrynan.aaaah.utils

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

fun classExists(fullName: String, messager: Messager): Boolean =
        try {
            Class.forName(fullName) != null
        } catch (e: ClassNotFoundException) {
            messager.printMessage(Diagnostic.Kind.NOTE, "JAVA_CLASS with name $fullName already exists. " +
                    "Either it was already created by the Annotation Processor or it exists in the Source Code. " +
                    "Not going to attempt to create another file.")
            false
        }