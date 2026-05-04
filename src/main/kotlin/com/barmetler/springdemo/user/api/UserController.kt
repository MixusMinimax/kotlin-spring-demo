package com.barmetler.springdemo.user.api

import com.barmetler.springdemo.user.api.dto.CreateUserRequest
import com.barmetler.springdemo.user.api.dto.UserDTO
import com.barmetler.springdemo.user.usecases.CreateUserUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val createUser: CreateUserUseCase
) {
    @GetMapping
    fun getUser(): String {
        return "Hello, World!"
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): UserDTO {
        return createUser.create(email = request.email, password = request.password)
    }
}
