package com.barmetler.springdemo.user.api

import com.barmetler.springdemo.user.api.dto.CreateUserRequest
import com.barmetler.springdemo.user.application.model.UserDTO
import com.barmetler.springdemo.user.application.usecase.CreateUserUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val createUser: CreateUserUseCase,
) {
    @GetMapping
    fun getUser(): String = "Hello, World!"

    @PostMapping
    fun createUser(
        @RequestBody request: CreateUserRequest,
    ): UserDTO = createUser.create(email = request.email, password = request.password)
}
