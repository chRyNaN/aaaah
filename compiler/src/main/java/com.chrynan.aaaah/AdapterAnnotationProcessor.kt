package com.chrynan.aaaah

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

typealias ClassFullName = String
typealias FieldName = String

@Suppress("unused")
@AutoService(Processor::class)
@SupportedOptions("com.chrynan.aaaah")
class AdapterAnnotationProcessor : AbstractProcessor() {

    companion object {

        private const val ANOTHER_ADAPTER_NAME = "AnotherAdapter"
        private const val ANOTHER_ADAPTER_FULL_NAME = "com.chrynan.aaaah.AnotherAdapter"
    }

    private val adapterViewTypeProviderClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AdapterViewTypesProvider") }
    private val anotherAdapterClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AnotherAdapter") }
    private val genericAnotherAdapterClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(anotherAdapterClassName, WildcardTypeName.subtypeOf(java.lang.Object::class.java)) }
    private val javaClassName: ClassName by lazy { ClassName.get("java.lang", "Class") }
    private val anotherAdapterGenericJavaClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(javaClassName, genericAnotherAdapterClassName) }
    private val mapClassName: ClassName by lazy { ClassName.get(java.util.Map::class.java) }
    private val integerClassName: ClassName by lazy { ClassName.get(java.lang.Integer::class.java) }
    private val viewTypesMapClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(mapClassName, anotherAdapterGenericJavaClassName, integerClassName) }
    private val adapterViewTypeSingularClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AdapterViewType") }
    private val adapterViewTypesPluralClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AdapterViewTypes") }


    // TODO Try creating a Java File as output because support for Kotlin Output sucks with Kapt
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val viewTypeNameMap = mutableMapOf<ClassFullName, FieldName>()

        roundEnv.getElementsAnnotatedWith(Adapter::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "The ${Adapter::class.java.simpleName} Annotation must be applied to a Class.")
            }

            val providedName = (it as TypeElement).getAnnotation(Adapter::class.java).name

            val packageName = processingEnv.elementUtils.getPackageOf(it).toString()
            val className = it.simpleName.toString()

            viewTypeNameMap["$packageName.$className"] = if (providedName.isBlank()) className.toConstantFieldName() else providedName
        }

        val adapterViewTypesSpecBuilder = TypeSpec.classBuilder("AdapterViewTypes")
                .addSuperinterface(adapterViewTypeProviderClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(FieldSpec.builder(adapterViewTypesPluralClassName, "singleton")
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new AdapterViewTypes()")
                        .build())
                .addField(FieldSpec.builder(viewTypesMapClassName, "map")
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new HashMap<Class<AnotherAdapter<?>>, Integer>()")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .returns(adapterViewTypesPluralClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .addCode("return singleton")
                        .build())

        val constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)

        var count = 0
        for (name in viewTypeNameMap.entries) {
            constructorBuilder.addStatement("map.put($name.key, $count)")

            adapterViewTypesSpecBuilder.addField(FieldSpec.builder(integerClassName, name.value)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$count")
                    .build())

            count += 1
        }

        adapterViewTypesSpecBuilder.addMethod(MethodSpec.methodBuilder("getViewTypes")
                .returns(viewTypesMapClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addCode("return map")
                .build())

        val adapterViewTypeSingularTypeSpecBuilder = TypeSpec.classBuilder("AdapterViewTypeKt")
                .addMethod(
                        MethodSpec.methodBuilder("from")
                                .returns(integerClassName)
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .addParameter(adapterViewTypeSingularClassName, "adapterViewType")
                                .addParameter(anotherAdapterGenericJavaClassName, "clazz")
                                .addCode("" +
                                        "Int viewType = AdapterViewTypes.getInstance().getViewTypes().get(clazz);" +
                                        "if (viewType == null) return -1 else return viewType;"
                                )
                                .build())

        val adapterViewTypesFile = JavaFile.builder("com.chrynan.aaaah", adapterViewTypesSpecBuilder.build()).build()
        val adapterViewTypeSingularFile = JavaFile.builder("com.chrynan.aaaah", adapterViewTypeSingularTypeSpecBuilder.build()).build()

        adapterViewTypesFile.writeTo(processingEnv.filer)
        adapterViewTypeSingularFile.writeTo(processingEnv.filer)

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