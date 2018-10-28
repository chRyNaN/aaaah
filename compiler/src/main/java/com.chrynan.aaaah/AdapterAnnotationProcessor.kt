package com.chrynan.aaaah

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@Suppress("unused")
@AutoService(Processor::class)
@SupportedOptions("com.chrynan.aaaah")
class AdapterAnnotationProcessor : AbstractProcessor() {

    companion object {

        private const val ANOTHER_ADAPTER_NAME = "AnotherAdapter"
        private const val ANOTHER_ADAPTER_FULL_NAME = "com.chrynan.aaaah.AnotherAdapter"
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val viewTypeNameMap = mutableMapOf<String, String>()

        roundEnv.getElementsAnnotatedWith(Adapter::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "The ${Adapter::class.java.simpleName} Annotation must be applied to a Class.")
            }

            val name = (it as TypeElement).getAnnotation(Adapter::class.java).name

            val packageName = processingEnv.elementUtils.getPackageOf(it).toString()
            val className = it.simpleName

            viewTypeNameMap["$packageName.$className"] = if (name.isBlank()) it.simpleName.toString().toConstantFieldName() else name
        }

        val mapStringBuilder = StringBuilder()

        val typeSpecBuilder = TypeSpec.objectBuilder("AdapterViewTypes")

        var count = 0
        for (name in viewTypeNameMap.entries) {
            mapStringBuilder.append("${name.key}::class.java to $count")

            typeSpecBuilder.addProperty(PropertySpec.builder(name = name.value, type = Int::class.java)
                    .addModifiers(KModifier.CONST)
                    .initializer("$count")
                    .build())

            count += 1
        }

        val anotherAdapterType = ClassName("com.chrynan.aaaah", "AnotherAdapter")
        val wildCardAnotherAdapterType = anotherAdapterType.parameterizedBy(WildcardTypeName.STAR)
        val javaClassType = ClassName("java.lang", "Class")
        val anotherAdapterJavaClassType = javaClassType.parameterizedBy(wildCardAnotherAdapterType)
        val mapType = ClassName("kotlin.collections", "Map")
        val mapOfAnotherAdapterType = mapType.parameterizedBy(anotherAdapterJavaClassType)
        val adapterViewTypeType = ClassName("com.chrynan.aaaah", "AdapterViewType")
        val kotlinClassType = ClassName("kotlin.reflect", "KClass")
        val kotlinClassAnotherAdapterType = kotlinClassType.parameterizedBy(wildCardAnotherAdapterType)
        val viewTypeType = ClassName("com.chrynan.aaaah", "ViewType")

        typeSpecBuilder.addProperty(PropertySpec.builder(name = "viewTypes", type = mapOfAnotherAdapterType)
                .initializer(
                        """
                        mapOf(
                            $mapStringBuilder
                        )
                        """.trimIndent())
                .build())

        val adapterFromFunction = FunSpec.builder("from")
                .returns(viewTypeType)
                .receiver(adapterViewTypeType)
                .addParameter(name = "clazz", type = kotlinClassAnotherAdapterType)
                .addStatement("return AdapterViewTypes.viewTypes[clazz.java] ?: -1")
                .build()

        val file = FileSpec.builder(packageName = "com.chrynan.aaaah", fileName = "AdapterViewTypes")
                .addType(typeSpec = typeSpecBuilder.build())
                .addFunction(adapterFromFunction)
                .build()

        file.writeTo(File(processingEnv.options["kapt.kotlin.generated"], "AdapterViewTypes"))

        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = mutableSetOf(Adapter::class.java.canonicalName)

    private fun String.toConstantFieldName(): String {
        val nameStringBuilder = StringBuilder()

        val chars = toCharArray()

        for (i in 0 until chars.size) {
            val char = chars[i]

            if (Character.isUpperCase(char) && i != 0) {
                nameStringBuilder.append("_$char")
            } else {
                nameStringBuilder.append(Character.toUpperCase(char))
            }
        }

        return nameStringBuilder.toString()
    }
}