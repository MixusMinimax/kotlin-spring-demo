package com.barmetler.springdemo.user.api

import com.barmetler.springdemo.user.api.dto.UserDTO
import com.barmetler.springdemo.user.api.dto.CreateUserRequest
import com.barmetler.springdemo.user.usecases.CreateUserUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    val createUser: CreateUserUseCase
) {
    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): UserDTO {
        return createUser.create(email = request.email, password = request.password)
    }
}
