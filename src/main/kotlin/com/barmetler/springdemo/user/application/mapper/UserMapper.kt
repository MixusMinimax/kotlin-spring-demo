package com.barmetler.springdemo.user.application.mapper

import com.barmetler.springdemo.user.application.model.UserDTO
import com.barmetler.springdemo.user.domain.model.User

fun User.toUserDTO(): UserDTO = UserDTO(
    id = checkNotNull(id),
    email = email,
)
