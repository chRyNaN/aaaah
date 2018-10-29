package com.chrynan.aaaah

import com.chrynan.aaaah.builder.AdapterViewTypeExtensionBuilder
import com.chrynan.aaaah.builder.AdapterViewTypesBuilder
import com.chrynan.aaaah.utils.classExists
import com.chrynan.aaaah.utils.createJavaFile
import com.chrynan.aaaah.utils.getAdapterAnnotatedClasses
import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@Suppress("unused")
@AutoService(Processor::class)
class AdapterAnnotationProcessor : AbstractProcessor() {

    private val filer: Filer by lazy { processingEnv.filer }
    private val messager: Messager by lazy { processingEnv.messager }
    private val elementUtils: Elements by lazy { processingEnv.elementUtils }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Only generate files on the first round. This is to avoid an error when attempting to recreate the generated file on different rounds.
        val adapterViewTypesAlreadyCreated = classExists(fullName = FullNames.ADAPTER_VIEW_TYPES, messager = messager)
        val adapterViewTypeExtensionKtAlreadyCreated = classExists(fullName = FullNames.ADAPTER_VIEW_TYPE_EXTENSION, messager = messager)

        if (!adapterViewTypesAlreadyCreated and !adapterViewTypeExtensionKtAlreadyCreated) {
            val annotatedClasses = roundEnv.getAdapterAnnotatedClasses(elementUtils = elementUtils, messager = messager)

            if (annotatedClasses.isNotEmpty()) {
                val adapterViewTypesTypeSpec = AdapterViewTypesBuilder(annotatedClasses = annotatedClasses).build()
                val adapterViewTypeExtensionTypeSpec = AdapterViewTypeExtensionBuilder().build()

                val adapterViewTypesFile = JavaFile.builder(Packages.AAAAH, adapterViewTypesTypeSpec).build()
                val adapterViewTypeExtensionFile = JavaFile.builder(Packages.AAAAH, adapterViewTypeExtensionTypeSpec).build()

                filer.createJavaFile(file = adapterViewTypesFile, fileName = FullNames.ADAPTER_VIEW_TYPES, messager = messager)
                filer.createJavaFile(file = adapterViewTypeExtensionFile, fileName = FullNames.ADAPTER_VIEW_TYPE, messager = messager)
            }
        }

        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = mutableSetOf(Adapter::class.java.canonicalName)
}