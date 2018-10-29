package com.chrynan.aaaah.utils

import com.chrynan.aaaah.Adapter
import com.chrynan.aaaah.model.AdapterAnnotatedClass
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

fun RoundEnvironment.getAdapterAnnotatedClasses(elementUtils: Elements, messager: Messager) =
        getElementsAnnotatedWith(Adapter::class.java).map {
            if (it.kind != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR, "The ${Adapter::class.java.simpleName} Annotation must be applied to a JAVA_CLASS.")
            }

            AdapterAnnotatedClass(
                    packageName = elementUtils.getPackageOf(it).toString(),
                    simpleName = it.simpleName.toString(),
                    providedName = (it as TypeElement).getAnnotation(Adapter::class.java).name)
        }