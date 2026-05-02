package com.barmetler.springdemo.user.api.dto

import io.swagger.v3.oas.annotations.media.Schema

data class LoginRequest(
    val user: UserIdentifier,
    @Schema(description = "let's see if this shows up.")
    val password: String,
)
