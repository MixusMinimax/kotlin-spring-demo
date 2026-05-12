package com.barmetler.springdemo.util.patch

import io.swagger.v3.oas.annotations.media.Schema

@Schema(hidden = true)
sealed interface Field<out T : Any> {
    @Schema(hidden = true)
    data object Missing : Field<Nothing>

    @Schema(hidden = true)
    data object Null : Field<Nothing>

    @Schema(hidden = true)
    data class Value<T : Any>(val value: T) : Field<T>

    companion object {
        fun <T : Any> missing(): Field<T> = Missing
        fun <T : Any> nullValue(): Field<T> = Null
        fun <T : Any> of(value: T): Field<T> = Value(value)
        fun <T : Any> ofNullable(value: T?): Field<T> = value?.let { Value(it) } ?: Null
    }
}
