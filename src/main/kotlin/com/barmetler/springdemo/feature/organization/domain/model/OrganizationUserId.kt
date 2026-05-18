package com.barmetler.springdemo.feature.organization.domain.model

import jakarta.persistence.Embeddable
import java.io.Serial
import java.io.Serializable
import java.util.UUID

@Embeddable
data class OrganizationUserId(
    var organizationId: UUID,
    var userId: UUID,
) : Serializable {
    companion object {
        @Serial
        private const val serialVersionUID: Long = 2896113088118971840L
    }
}
