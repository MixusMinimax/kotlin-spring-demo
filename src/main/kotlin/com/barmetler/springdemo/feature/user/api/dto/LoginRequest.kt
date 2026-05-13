package com.barmetler.springdemo.feature.user.api.dto

import com.barmetler.springdemo.feature.user.application.model.UserIdentifier
import io.swagger.v3.oas.annotations.media.Schema

data class LoginRequest(
    val user: UserIdentifier,
    @Schema(description = "let's see if this shows up.")
    val password: String,
)
