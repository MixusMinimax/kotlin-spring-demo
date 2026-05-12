package com.barmetler.springdemo.util.patch

import tools.jackson.core.JsonParser
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.ValueDeserializer

class FieldDeserializer(val javaType: JavaType? = null) : ValueDeserializer<Field<*>>() {

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): Field<*> {
        require(javaType?.rawClass == Field::class.java) {
            "FieldDeserializer can only be used to deserialize into Field."
        }
        if (p.currentToken() == null) p.nextToken()
        if (p.currentToken() == null) return Field.Missing

        return Field.ofNullable(ctxt.readValue<Any>(p, javaType.containedType(0)))
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty) =
        FieldDeserializer(
            property.type,
        )

    override fun getNullValue(ctx: DeserializationContext) = Field.Null
}
