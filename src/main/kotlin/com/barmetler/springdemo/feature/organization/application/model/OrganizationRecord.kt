package com.barmetler.springdemo.feature.organization.application.model

import java.util.UUID

data class OrganizationRecord(val id: UUID, val slug: String, val name: String)
