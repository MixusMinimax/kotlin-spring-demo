package com.barmetler.springdemo.feature.organization.infrastructure.repository

import com.barmetler.springdemo.feature.organization.domain.model.Organization

interface OrganizationRepositoryCustom {
    data class PaginatedOrganizationList(
        val organizations: List<Organization>,
        val hasMore: Boolean = false,
        val lastSlugOfPage: String? = null,
    )

    /**
     * Keyset pagination.
     */
    fun findAllPaginated(slugAfter: String? = null, maxCount: Int? = null): PaginatedOrganizationList
}
