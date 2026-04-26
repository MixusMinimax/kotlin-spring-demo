package com.barmetler.springdemo.user.api.dto

import com.barmetler.springdemo.user.domain.User

fun User.toUserDTO(): UserDTO = UserDTO(
    id = checkNotNull(id),
    email = email,
)
