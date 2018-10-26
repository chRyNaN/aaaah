package com.chrynan.aaaah

import com.google.auto.service.AutoService
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

            viewTypeNameMap[packageName + className] = if (name.isBlank()) it.simpleName.toString().toConstantFieldName() else name
        }

        val adapterViewTypesResultStringBuilder = StringBuilder()
        val constantFieldsStringBuilder = StringBuilder()
        val mapStringBuilder = StringBuilder()

        var count = 0
        for (name in viewTypeNameMap.entries) {
            constantFieldsStringBuilder.append("const val ${name.value} = $count")
            mapStringBuilder.append("${name.key} to $count")
            count += 1
        }

        adapterViewTypesResultStringBuilder.append("package com.chrynan.aaaah\n\n")
        adapterViewTypesResultStringBuilder.append("object AdapterViewTypes : AdapterViewTypesProvider {\n\n")
        adapterViewTypesResultStringBuilder.append("companion object {\n\n")
        adapterViewTypesResultStringBuilder.append(constantFieldsStringBuilder)
        adapterViewTypesResultStringBuilder.append("}\n\n")
        adapterViewTypesResultStringBuilder.append("val viewTypes: Map<AnotherAdapter<*>, ViewType> = mapOf(\n")
        adapterViewTypesResultStringBuilder.append(mapStringBuilder)
        adapterViewTypesResultStringBuilder.append(")\n")
        adapterViewTypesResultStringBuilder.append("}\n\n")
        adapterViewTypesResultStringBuilder.append("inline fun <reified T : Any> AdapterViewType.from(clazz: KClass<AnotherAdapter>): ViewType = AdapterViewTypes.viewTypes[clazz.qualifiedName] ?: -1")

        val writer = processingEnv.filer.createSourceFile("com.chrynan.aaaah.AdapterViewTypes").openWriter()
        writer.write(adapterViewTypesResultStringBuilder.toString())
        writer.close()

        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = mutableSetOf(Adapter::class.java.canonicalName)

    private fun String.toConstantFieldName(): String {
        val nameStringBuilder = StringBuilder("AdapterViewTypes.")

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