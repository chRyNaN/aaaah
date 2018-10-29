package com.chrynan.aaaah.builder

import com.chrynan.aaaah.Names
import com.chrynan.aaaah.Packages
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class KotlinAdapterViewTypeExtensionBuilder : KotlinFileSpecBuilder {

    private val anotherAdapterClassName: ClassName by lazy { ClassName(packageName = Packages.AAAAH, simpleName = Names.ANOTHER_ADAPTER) }
    private val genericAnotherAdapterClassName: ParameterizedTypeName by lazy { anotherAdapterClassName.parameterizedBy(WildcardTypeName.STAR) }
    private val javaClassName: ClassName by lazy { ClassName(packageName = Packages.JAVA_CLASS, simpleName = Names.JAVA_CLASS) }
    private val anotherAdapterGenericJavaClassName: ParameterizedTypeName by lazy { javaClassName.parameterizedBy(genericAnotherAdapterClassName) }
    private val adapterViewTypeSingularClassName: ClassName by lazy { ClassName(packageName = Packages.AAAAH, simpleName = Names.ADAPTER_VIEW_TYPE) }
    private val adapterViewTypesClassName: ClassName by lazy { ClassName(packageName = Packages.AAAAH, simpleName = Names.ADAPTER_VIEW_TYPES) }
    private val viewTypeClassName: ClassName by lazy { ClassName(packageName = Packages.AAAAH, simpleName = Names.VIEW_TYPE) }

    override fun build(): FileSpec =
            FileSpec.builder(packageName = Packages.AAAAH, fileName = Names.ADAPTER_VIEW_TYPE_EXTENSION)
                    .addFunction(FunSpec.builder("from")
                            .receiver(adapterViewTypeSingularClassName)
                            .returns(viewTypeClassName)
                            .addParameter(name = "clazz", type = anotherAdapterGenericJavaClassName)
                            .addStatement("return %T.viewTypes[clazz] ?: -1", adapterViewTypesClassName)
                            .build())
                    .build()
}