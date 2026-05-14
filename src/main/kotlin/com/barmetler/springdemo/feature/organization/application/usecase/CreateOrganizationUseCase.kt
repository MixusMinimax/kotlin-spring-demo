package com.barmetler.springdemo.feature.organization.application.usecase

import com.barmetler.springdemo.feature.organization.application.mapper.toOrganizationRecord
import com.barmetler.springdemo.feature.organization.application.model.OrganizationRecord
import com.barmetler.springdemo.feature.organization.domain.model.Organization
import com.barmetler.springdemo.feature.organization.infrastructure.repository.OrganizationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = false)
class CreateOrganizationUseCase(
    private val organizationRepository: OrganizationRepository,
) {
    fun create(slug: String, name: String): OrganizationRecord {
        val org = Organization(slug = slug, name = name)
        val saved = organizationRepository.save(org)
        return saved.toOrganizationRecord()
    }
}
