package com.barmetler.springdemo.user.api.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
sealed interface UserIdentifier {
    data class Id(val id: UUID) : UserIdentifier
    data class Email(val email: String) : UserIdentifier
}
