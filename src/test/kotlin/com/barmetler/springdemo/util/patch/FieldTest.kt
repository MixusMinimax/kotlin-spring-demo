package com.barmetler.springdemo.util.patch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class FieldTest : StringSpec({
    "ifSet should not invoke block for Missing" {
        val results = mutableListOf<String>()

        Field.missing<String>().ifSet {
            results += "called"
        }

        results shouldBe emptyList()
    }

    "ifSet should invoke block with null for Null" {
        var result: String? = "initial"

        Field.nullValue<String>().ifSet {
            result = it
        }

        result shouldBe null
    }

    "ifSet should invoke block with value for Value" {
        var result: String? = null

        Field.of("hello").ifSet {
            result = it
        }

        result shouldBe "hello"
    }

    "ofNullable should create Value when non-null" {
        val field = Field.ofNullable("abc")

        field shouldBe Field.Value("abc")
    }

    "ofNullable should create Null when null" {
        val field = Field.ofNullable<String>(null)

        field shouldBe Field.Null
    }

    "ifSet should support multiple field states consistently" {
        val results = mutableListOf<String?>()

        Field.missing<String>().ifSet { results += it }
        Field.nullValue<String>().ifSet { results += it }
        Field.of("value").ifSet { results += it }

        results shouldContainExactly listOf(null, "value")
    }
})
