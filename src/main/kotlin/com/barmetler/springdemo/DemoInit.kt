package com.barmetler.springdemo

import com.barmetler.springdemo.user.usecases.CreateUserUseCase
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.dao.DataIntegrityViolationException

@Configuration
@Profile("dev")
class DemoInit {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun initDatabase(createUser: CreateUserUseCase): CommandLineRunner = CommandLineRunner {
        try {
            createUser.create(email = "demo@example.com", password = "password123")
        } catch (ex: DataIntegrityViolationException) {
            log.warn("creating demo user failed.", ex)
        }
    }
}
