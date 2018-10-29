package com.chrynan.aaaah

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
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
        private const val ADAPTER_VIEW_TYPES_FULL_NAME = "com.chrynan.aaaah.AdapterViewTypes"
        private const val ADAPTER_VIEW_TYPE_EXTENSION_FULL_NAME = "com.chrynan.aaaah.AdapterViewTypeExtensionKt"
    }

    private val filer: Filer by lazy { processingEnv.filer }
    private val messager: Messager by lazy { processingEnv.messager }
    private val elementUtils: Elements by lazy { processingEnv.elementUtils }

    private val adapterViewTypeProviderClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AdapterViewTypesProvider") }
    private val anotherAdapterClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AnotherAdapter") }
    private val genericAnotherAdapterClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(anotherAdapterClassName, WildcardTypeName.subtypeOf(java.lang.Object::class.java)) }
    private val javaClassName: ClassName by lazy { ClassName.get("java.lang", "Class") }
    private val anotherAdapterGenericJavaClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(javaClassName, WildcardTypeName.subtypeOf(genericAnotherAdapterClassName)) }
    private val mapClassName: ClassName by lazy { ClassName.get(java.util.Map::class.java) }
    private val integerClassName: ClassName by lazy { ClassName.get(java.lang.Integer::class.java) }
    private val viewTypesMapClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(mapClassName, anotherAdapterGenericJavaClassName, integerClassName) }
    private val adapterViewTypeSingularClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AdapterViewType") }
    private val adapterViewTypesPluralClassName: ClassName by lazy { ClassName.get("com.chrynan.aaaah", "AdapterViewTypes") }
    private val hashMapClassName: ClassName by lazy { ClassName.get(HashMap::class.java) }
    private val viewTypesHashMapClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(hashMapClassName, anotherAdapterGenericJavaClassName, integerClassName) }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val viewTypeNameMap = mutableMapOf<ClassFullName, FieldName>()
        val adapterViewTypesAlreadyCreated = classExists(fullName = "$ADAPTER_VIEW_TYPES_FULL_NAME") {
            messager.printMessage(Diagnostic.Kind.WARNING, "Class with name $ADAPTER_VIEW_TYPES_FULL_NAME already exists. " +
                    "Either it was already created by the Annotation Processor or it exists in the Source Code." +
                    "Not going to attempt to create another file.")
        }
        val adapterViewTypeExtensionKtAlreadyCreated = classExists(fullName = "$ADAPTER_VIEW_TYPE_EXTENSION_FULL_NAME") {
            messager.printMessage(Diagnostic.Kind.WARNING, "Class with name $ADAPTER_VIEW_TYPE_EXTENSION_FULL_NAME already exists. " +
                    "Either it was already created by the Annotation Processor or it exists in the Source Code." +
                    "Not going to attempt to create another file.")
        }

        if (!adapterViewTypesAlreadyCreated and !adapterViewTypeExtensionKtAlreadyCreated) {
            roundEnv.getElementsAnnotatedWith(Adapter::class.java).forEach {
                if (it.kind != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "The ${Adapter::class.java.simpleName} Annotation must be applied to a Class.")
                }

                val providedName = (it as TypeElement).getAnnotation(Adapter::class.java).name

                val packageName = elementUtils.getPackageOf(it).toString()
                val className = it.simpleName.toString()

                viewTypeNameMap["$packageName.$className"] = if (providedName.isBlank()) className.toConstantFieldName() else providedName
            }

            if (viewTypeNameMap.isNotEmpty()) {
                val adapterViewTypesSpecBuilder = TypeSpec.classBuilder("AdapterViewTypes")
                        .addSuperinterface(adapterViewTypeProviderClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(FieldSpec.builder(adapterViewTypesPluralClassName, "singleton")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                .initializer("new AdapterViewTypes()")
                                .build())
                        .addField(FieldSpec.builder(viewTypesMapClassName, "map")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                .initializer("new \$T()", viewTypesHashMapClassName)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("getInstance")
                                .returns(adapterViewTypesPluralClassName)
                                .addAnnotation(NotNull::class.java)
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .addCode("return singleton;\n")
                                .build())

                val constructorBuilder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)

                var count = 0
                for (name in viewTypeNameMap.entries) {
                    constructorBuilder.addStatement("map.put(${name.key}.class, $count)")

                    adapterViewTypesSpecBuilder.addField(FieldSpec.builder(integerClassName, name.value)
                            .addAnnotation(NotNull::class.java)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$count")
                            .build())

                    count += 1
                }

                adapterViewTypesSpecBuilder.addMethod(constructorBuilder.build())

                adapterViewTypesSpecBuilder.addMethod(MethodSpec.methodBuilder("getViewTypes")
                        .returns(viewTypesMapClassName)
                        .addAnnotation(NotNull::class.java)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addCode("return map;\n")
                        .build())

                val adapterViewTypeSingularTypeSpecBuilder = TypeSpec.classBuilder("AdapterViewTypeExtensionKt")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(
                                MethodSpec.methodBuilder("from")
                                        .returns(integerClassName)
                                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                        .addAnnotation(NotNull::class.java)
                                        .addParameter(adapterViewTypeSingularClassName, "adapterViewType")
                                        .addParameter(anotherAdapterGenericJavaClassName, "clazz")
                                        .addCode("" +
                                                "Integer viewType = AdapterViewTypes.getInstance().getViewTypes().get(clazz);\n" +
                                                "if (viewType == null) {\n" +
                                                "    return -1;\n" +
                                                "} else {\n" +
                                                "    return viewType;\n" +
                                                "}"
                                        )
                                        .build())

                val adapterViewTypesFile = JavaFile.builder("com.chrynan.aaaah", adapterViewTypesSpecBuilder.build()).build()
                val adapterViewTypeSingularFile = JavaFile.builder("com.chrynan.aaaah", adapterViewTypeSingularTypeSpecBuilder.build()).build()

                try {
                    adapterViewTypesFile.writeTo(filer)
                } catch (e: Exception) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Error creating AdapterViewTypes class.\n" +
                            "LocalizedMessage = ${e.localizedMessage}\n" +
                            "Exception = $e")
                }

                try {
                    adapterViewTypeSingularFile.writeTo(filer)
                } catch (e: Exception) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Error creating AdapterViewTypeExtensionKt class.\n" +
                            "LocalizedMessage = ${e.localizedMessage}\n" +
                            "Exception = $e")
                }
            }
        }

        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = mutableSetOf(Adapter::class.java.canonicalName)
}