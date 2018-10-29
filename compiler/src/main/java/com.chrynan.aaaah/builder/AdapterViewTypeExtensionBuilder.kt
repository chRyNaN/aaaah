package com.chrynan.aaaah.builder

import com.chrynan.aaaah.Names
import com.chrynan.aaaah.Packages
import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import javax.lang.model.element.Modifier

class AdapterViewTypeExtensionBuilder : JavaTypeSpecBuilder {

    private val anotherAdapterClassName: ClassName by lazy { ClassName.get(Packages.AAAAH, Names.ANOTHER_ADAPTER) }
    private val genericAnotherAdapterClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(anotherAdapterClassName, WildcardTypeName.subtypeOf(java.lang.Object::class.java)) }
    private val javaClassName: ClassName by lazy { ClassName.get(java.lang.Class::class.java) }
    private val anotherAdapterGenericJavaClassName: ParameterizedTypeName by lazy { ParameterizedTypeName.get(javaClassName, WildcardTypeName.subtypeOf(genericAnotherAdapterClassName)) }
    private val adapterViewTypeSingularClassName: ClassName by lazy { ClassName.get(Packages.AAAAH, Names.ADAPTER_VIEW_TYPE) }

    override fun build(): TypeSpec =
            TypeSpec.classBuilder(Names.ADAPTER_VIEW_TYPE_EXTENSION)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(
                            MethodSpec.methodBuilder("from")
                                    .returns(TypeName.INT)
                                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                    .addParameter(ParameterSpec.builder(adapterViewTypeSingularClassName, "\$receiver")
                                            .addAnnotation(NotNull::class.java)
                                            .build())
                                    .addParameter(ParameterSpec.builder(anotherAdapterGenericJavaClassName, "clazz")
                                            .addAnnotation(NotNull::class.java)
                                            .build())
                                    .addCode("" +
                                            "Integer viewType = ${Names.ADAPTER_VIEW_TYPES}.getInstance().getViewTypes().get(clazz);\n" +
                                            "if (viewType == null) {\n" +
                                            "    return -1;\n" +
                                            "} else {\n" +
                                            "    return viewType;\n" +
                                            "}"
                                    )
                                    .build())
                    .build()
}