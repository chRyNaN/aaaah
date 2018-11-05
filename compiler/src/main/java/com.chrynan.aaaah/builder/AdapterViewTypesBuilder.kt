package com.chrynan.aaaah.builder

import com.chrynan.aaaah.Names
import com.chrynan.aaaah.Packages
import com.chrynan.aaaah.model.AdapterAnnotatedClass
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import java.util.*
import javax.lang.model.element.Modifier

class AdapterViewTypesBuilder(private val annotatedClasses: List<AdapterAnnotatedClass>) : JavaTypeSpecBuilder {

    private val adapterViewTypeProviderClassName: ClassName by lazy { ClassName.get(Packages.AAAAH, Names.ADAPTER_VIEW_TYPES_PROVIDER) }
    private val anotherAdapterClassName: ClassName by lazy { ClassName.get(Packages.AAAAH, Names.ANOTHER_ADAPTER) }
    private val genericAnotherAdapterClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(anotherAdapterClassName, WildcardTypeName.subtypeOf(java.lang.Object::class.java)) }
    private val javaClassName: ClassName by lazy { ClassName.get(java.lang.Class::class.java) }
    private val anotherAdapterGenericJavaClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(javaClassName, WildcardTypeName.subtypeOf(genericAnotherAdapterClassName)) }
    private val mapClassName: ClassName by lazy { ClassName.get(java.util.Map::class.java) }
    private val integerClassName: ClassName by lazy { ClassName.get(java.lang.Integer::class.java) }
    private val viewTypesMapClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(mapClassName, anotherAdapterGenericJavaClassName, integerClassName) }
    private val adapterViewTypesPluralClassName: ClassName by lazy { ClassName.get(Packages.AAAAH, Names.ADAPTER_VIEW_TYPES) }
    private val hashMapClassName: ClassName by lazy { ClassName.get(HashMap::class.java) }
    private val viewTypesHashMapClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(hashMapClassName, anotherAdapterGenericJavaClassName, integerClassName) }

    override fun build(): TypeSpec =
            TypeSpec.classBuilder(Names.ADAPTER_VIEW_TYPES)
                    .addSuperinterface(adapterViewTypeProviderClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .createAndInitializeSingleton()
                    .createMap()
                    .initializeFieldsAndMap()
                    .createGetViewTypesMethod()
                    .build()

    private fun TypeSpec.Builder.createAndInitializeSingleton() =
            addField(FieldSpec.builder(adapterViewTypesPluralClassName, "singleton")
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new ${Names.ADAPTER_VIEW_TYPES}()")
                    .build())
                    .addMethod(MethodSpec.methodBuilder("getInstance")
                            .returns(adapterViewTypesPluralClassName)
                            .addAnnotation(NotNull::class.java)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .addCode("return singleton;\n")
                            .build())

    private fun TypeSpec.Builder.createMap() =
            addField(FieldSpec.builder(viewTypesMapClassName, "map")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .initializer("new \$T()", viewTypesHashMapClassName)
                    .build())

    private fun TypeSpec.Builder.initializeFieldsAndMap(): TypeSpec.Builder {
        val constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)

        var count = 0
        for (annotatedClass in annotatedClasses) {
            constructorBuilder.addStatement("map.put(${annotatedClass.fullNameWithClassSuffix}, $count)")

            addField(FieldSpec.builder(integerClassName, annotatedClass.fieldName)
                    .addAnnotation(NotNull::class.java)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$count")
                    .build())

            count += 1
        }

        addMethod(constructorBuilder.build())

        return this
    }

    private fun TypeSpec.Builder.createGetViewTypesMethod() =
            addMethod(MethodSpec.methodBuilder("getViewTypes")
                    .returns(viewTypesMapClassName)
                    .addAnnotation(NotNull::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override::class.java)
                    .addCode("return map;\n")
                    .build())
}