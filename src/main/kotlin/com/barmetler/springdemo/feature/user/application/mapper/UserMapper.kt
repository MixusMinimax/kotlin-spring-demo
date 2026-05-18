package com.barmetler.springdemo.feature.user.application.mapper

import com.barmetler.springdemo.feature.user.application.model.UserRecord
import com.barmetler.springdemo.feature.user.domain.model.User

fun User.toUserRecord(): UserRecord = UserRecord(
    id = checkNotNull(id),
    email = email,
)
