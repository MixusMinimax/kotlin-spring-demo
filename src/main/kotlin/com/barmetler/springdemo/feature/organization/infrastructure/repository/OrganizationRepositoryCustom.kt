package com.barmetler.springdemo.feature.organization.infrastructure.repository

import com.barmetler.springdemo.feature.organization.domain.model.Organization

interface OrganizationRepositoryCustom {
    fun findAllPaginated(slugAfter: String? = null, maxCount: Int? = null): List<Organization>
}
