package com.barmetler.springdemo.feature.organization.application.model

data class PaginatedOrganizationRecordList(
    val organizations: List<OrganizationRecord>,
    val hasMore: Boolean = false,
    val lastSlugOfPage: String? = null,
)
