package com.barmetler.springdemo.feature.organization.api.dto

import java.util.UUID

data class AddUserToOrganizationRequest(
    val organizationId: UUID,
    val userId: UUID,
)
