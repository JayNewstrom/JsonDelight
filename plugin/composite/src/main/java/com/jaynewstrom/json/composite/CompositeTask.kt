package com.jaynewstrom.json.composite

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaynewstrom.json.compiler.JsonCompiler
import com.jaynewstrom.json.compiler.VERSION
import com.jaynewstrom.json.runtime.JsonDeserializerFactory
import com.jaynewstrom.json.runtime.JsonSerializerFactory
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File
import javax.lang.model.element.Modifier

open class CompositeTask : SourceTask() {
    @Suppress("unused") // Required to invalidate the task on version updates.
    @Input fun pluginVersion() = VERSION

    @get:OutputDirectory var outputDirectory: File? = null

    var buildDirectory: File? = null
        set(value) {
            field = value
            outputDirectory = JsonCompiler.COMPOSITE_OUTPUT_DIRECTORY.fold(buildDirectory, ::File)
        }

    @TaskAction fun execute(inputs: IncrementalTaskInputs) {
        val serializers = mutableSetOf<TypeName>()
        val deserializers = mutableSetOf<TypeName>()
        getInputs().files.forEach { file ->
            if (file.name == "JsonSerializers.json") {
                serializers.addAll(getTypeNames(file, "serializers"))
            } else if (file.name == "JsonDeserializers.json") {
                deserializers.addAll(getTypeNames(file, "deserializers"))
            } else {
                throw IllegalStateException("Unknown file name.")
            }
        }
        writeFactory(serializers, "Serializer", JsonSerializerFactory::class.java)
        writeFactory(deserializers, "Deserializer", JsonDeserializerFactory::class.java)
    }

    private fun getTypeNames(file: File, attributeName: String): Set<TypeName> {
        val json = ObjectMapper().readTree(file)
        if (json.get("version").asText() != VERSION) {
            throw IllegalStateException("Files were generated using a different version.")
        }
        val typeNames = mutableSetOf<TypeName>()
        json.get(attributeName).forEach { typeName ->
            typeNames.add(ClassName.get(typeName.get("packageName").asText(), typeName.get("className").asText()))
        }
        return typeNames
    }

    private fun writeFactory(types: Set<TypeName>, kind: String, superClass: Class<*>) {
        val builder = TypeSpec.classBuilder("CompositeJson" + kind + "Factory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(superClass)

        val constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
        constructor.addStatement("super(\$L)", Math.round(types.size * 1.4))
        types.forEach { type ->
            constructor.addStatement("register(new \$T())", type)
        }
        builder.addMethod(constructor.build())

        val file = JavaFile.builder("com.jaynewstrom.json.runtime", builder.build()).build()
        file.writeTo(outputDirectory)
    }
}
