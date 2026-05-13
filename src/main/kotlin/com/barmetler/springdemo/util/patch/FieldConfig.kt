package com.barmetler.springdemo.util.patch

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.JacksonModule
import tools.jackson.databind.module.SimpleModule

@Configuration
class FieldConfig {

    @Bean
    fun fieldModule(): JacksonModule = SimpleModule().addDeserializer(Field::class.java, FieldDeserializer())

    @Bean
    fun fieldPropertyCustomizer() = FieldPropertyCustomizer()
}
