package com.barmetler.springdemo.user.application.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
sealed interface UserIdentifier {
    data class Id(val id: UUID) : UserIdentifier
    data class Email(val email: String) : UserIdentifier
}
