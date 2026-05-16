package com.barmetler.springdemo.feature.organization.application.usecase

import com.barmetler.springdemo.feature.organization.application.mapper.toOrganizationRecord
import com.barmetler.springdemo.feature.organization.application.model.PaginatedOrganizationRecordList
import com.barmetler.springdemo.feature.organization.infrastructure.repository.OrganizationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ListOrganizationsUseCase(
    private val organizationRepository: OrganizationRepository,
) {
    fun list(slugAfter: String? = null, maxCount: Int? = null): PaginatedOrganizationRecordList {
        val (organizations, hasMore, lastSlugOfPage) = organizationRepository.findAllPaginated(
            slugAfter = slugAfter,
            maxCount = maxCount,
        )
        return PaginatedOrganizationRecordList(
            organizations = organizations.map { it.toOrganizationRecord() },
            hasMore = hasMore,
            lastSlugOfPage = lastSlugOfPage,
        )
    }
}
