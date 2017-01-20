package com.jaynewstrom.json.compiler

import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

data class DeserializerFactoryBuilder(val deserializers: Collection<TypeName>) {
    fun build(): TypeSpec {
        return TypeSpec.classBuilder("RealJsonDeserializerFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ClassName.get(JsonDeserializerFactory::class.java))
                .addMethod(createConstructor())
                .build()
    }

    private fun createConstructor(): MethodSpec {
        val constructorBuilder = MethodSpec.constructorBuilder()
        constructorBuilder.addModifiers(Modifier.PUBLIC)
        deserializers.forEach {
            val codeFormat = "register(new \$T())"
            constructorBuilder.addStatement(codeFormat, it)
        }
        return constructorBuilder.build()
    }
}