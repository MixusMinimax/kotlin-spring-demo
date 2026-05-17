package com.barmetler.springdemo.feature.organization.api.dto

data class CreateOrganizationRequest(
    val slug: String,
    val name: String,
)
