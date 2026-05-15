package com.barmetler.springdemo.feature.organization.application.usecase

import com.barmetler.springdemo.feature.organization.application.mapper.toOrganizationRecord
import com.barmetler.springdemo.feature.organization.application.model.OrganizationRecord
import com.barmetler.springdemo.feature.organization.infrastructure.repository.OrganizationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ListOrganizationsUseCase(
    private val organizationRepository: OrganizationRepository,
) {
    fun list(slugAfter: String? = null, maxCount: Int? = null): List<OrganizationRecord> {
        val organizations = organizationRepository.findAllPaginated(slugAfter = slugAfter, maxCount = maxCount)
        return organizations.map { it.toOrganizationRecord() }
    }
}
