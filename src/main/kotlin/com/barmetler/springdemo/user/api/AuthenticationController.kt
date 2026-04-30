package com.barmetler.springdemo.user.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController {
    @GetMapping("/refresh")
    fun refresh(): String {
        return "hello"
    }
}
