package com.jaynewstrom.json.compiler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import java.io.File
import java.util.ArrayList

data class JsonModelDefinitionParser(val file: File, val createSerializerByDefault: Boolean,
        val createDeserializerByDefault: Boolean, val useAutoValueByDefault: Boolean) {
    fun parse(): ModelDefinition {
        val objectMapper = ObjectMapper()
        val modelJson = objectMapper.readTree(file)
        val modelName = JsonCompiler.nameFromFile(file)
        val isPublic = modelJson.getBooleanOrDefault("public", false)
        val fieldDefinitions = ArrayList<FieldDefinition>()
        modelJson.get("fields").forEach {
            fieldDefinitions.add(parseField(it, isPublic))
        }
        val createSerializer = modelJson.getBooleanOrDefault("createSerializer", createSerializerByDefault)
        val createDeserializer = modelJson.getBooleanOrDefault("createDeserializer", createDeserializerByDefault)
        val useAutoValue = modelJson.getBooleanOrDefault("useAutoValue", useAutoValueByDefault)
        return ModelDefinition(isPublic, modelName, fieldDefinitions, createSerializer, createDeserializer, useAutoValue)
    }

    private fun parseField(fieldJson: JsonNode, modelIsPublic: Boolean): FieldDefinition {
        val isPublic = fieldJson.getBooleanOrDefault("public", modelIsPublic)
        val fieldName = fieldJson.get("name").asText()
        val jsonName = if (fieldJson.has("jsonName")) fieldJson.get("jsonName").asText() else fieldName
        val typeName = fieldJson.get("type").asText()
        var optionalType = PrimitiveType.typeNameFromIdentifier(typeName)
        if (optionalType == null) {
            optionalType = ClassName.bestGuess(typeName)
        }
        val isList = fieldJson.getBooleanOrDefault("isList", false)
        if (isList) {
            optionalType = ParameterizedTypeName.get(ClassName.get("java.util", "List"), optionalType!!)
        }
        val type = optionalType!!
        val isRequired = fieldJson.getBooleanOrDefault("required", !type.isPrimitive)
        if (isRequired && type.isPrimitive) {
            throw IllegalStateException("Primitives can't be required.")
        }
        var customSerializer: TypeName? = null
        if (fieldJson.hasNonNull("customSerializer")) {
            customSerializer = ClassName.bestGuess(fieldJson.get("customSerializer").asText())
        }
        var customDeserializer: TypeName? = null
        if (fieldJson.hasNonNull("customDeserializer")) {
            customDeserializer = ClassName.bestGuess(fieldJson.get("customDeserializer").asText())
        }
        return FieldDefinition(isPublic, isRequired, type, fieldName, jsonName, customSerializer, customDeserializer)
    }

    private fun JsonNode.getBooleanOrDefault(key: String, defaultValue: Boolean): Boolean {
        return if (has(key)) get(key).asBoolean() else defaultValue
    }
}