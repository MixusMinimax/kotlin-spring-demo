package com.barmetler.springdemo.util.patch

import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.media.JsonSchema
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.customizers.PropertyCustomizer

class FieldPropertyCustomizer : PropertyCustomizer {
    override fun customize(
        property: Schema<*>,
        type: AnnotatedType,
    ): Schema<*> {
        val javaType = type.type

        if (javaType is com.fasterxml.jackson.databind.JavaType &&
            javaType.rawClass == Field::class.java
        ) {
            val inner = javaType.containedType(0)

            val resolved = ModelConverters.getInstance()
                .resolveAsResolvedSchema(AnnotatedType(inner))

            val replacement = resolved.schema

            val schema = Schema<Any?>()
            schema.oneOf =
                mutableListOf(replacement, JsonSchema().types(mutableSetOf("null")).description("hello"))
            schema.nullable = true

            return schema
        }
        return property
    }
}
