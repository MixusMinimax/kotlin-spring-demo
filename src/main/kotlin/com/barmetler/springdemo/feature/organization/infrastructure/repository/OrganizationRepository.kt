package com.barmetler.springdemo.feature.organization.infrastructure.repository

import com.barmetler.springdemo.feature.organization.domain.model.Organization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrganizationRepository : JpaRepository<Organization, UUID>
