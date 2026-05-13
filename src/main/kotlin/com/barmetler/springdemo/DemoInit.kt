package com.barmetler.springdemo

import com.barmetler.springdemo.user.application.usecase.CreateUserUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.dao.DataIntegrityViolationException

@Configuration
@Profile("dev")
class DemoInit {
    private val logger = KotlinLogging.logger { }

    @Bean
    fun initDatabase(createUser: CreateUserUseCase): CommandLineRunner = CommandLineRunner {
        try {
            createUser.create(email = "demo@example.com", password = "password123")
        } catch (ex: DataIntegrityViolationException) {
            logger.warn(ex) { "creating demo user failed." }
        }
    }
}
