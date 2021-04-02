package com.chrynan.aaaah.builder

import com.chrynan.aaaah.Names
import com.chrynan.aaaah.Packages
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class KotlinAdapterViewTypeExtensionBuilder : KotlinFileSpecBuilder {

    private val anotherAdapterClassName: ClassName by lazy {
        ClassName(
            Packages.AAAAH,
            Names.ANOTHER_ADAPTER
        )
    }
    private val genericAnotherAdapterClassName: ParameterizedTypeName by lazy {
        anotherAdapterClassName.parameterizedBy(
            STAR
        )
    }
    private val javaClassName: ClassName by lazy {
        ClassName(
            Packages.JAVA_CLASS,
            Names.JAVA_CLASS
        )
    }
    private val adapterViewTypeSingularClassName: ClassName by lazy {
        ClassName(
            Packages.AAAAH,
            Names.ADAPTER_VIEW_TYPE
        )
    }
    private val adapterViewTypesClassName: ClassName by lazy {
        ClassName(
            Packages.AAAAH,
            Names.ADAPTER_VIEW_TYPES
        )
    }
    private val viewTypeClassName: ClassName by lazy { ClassName(Packages.AAAAH, Names.VIEW_TYPE) }
    private val typeVariableName: TypeVariableName by lazy {
        TypeVariableName(
            "T",
            genericAnotherAdapterClassName
        )
    }
    private val anotherAdapterGenericJavaClassName: ParameterizedTypeName by lazy {
        javaClassName.parameterizedBy(
            typeVariableName
        )
    }

    override fun build(): FileSpec =
        FileSpec.builder(packageName = Packages.AAAAH, fileName = Names.ADAPTER_VIEW_TYPE_EXTENSION)
            .addFunction(
                FunSpec.builder("from")
                    .receiver(adapterViewTypeSingularClassName)
                    .returns(viewTypeClassName)
                    .addTypeVariable(typeVariableName)
                    .addParameter(name = "clazz", type = anotherAdapterGenericJavaClassName)
                    .addStatement(
                        "return %T.getInstance().viewTypes[clazz] ?: -1",
                        adapterViewTypesClassName
                    )
                    .build()
            )
            .build()
}