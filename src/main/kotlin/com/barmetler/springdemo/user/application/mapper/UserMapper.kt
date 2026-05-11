package com.barmetler.springdemo.user.application.mapper

import com.barmetler.springdemo.user.application.model.UserRecord
import com.barmetler.springdemo.user.domain.model.User

fun User.toUserDTO(): UserRecord = UserRecord(
    id = checkNotNull(id),
    email = email,
)
