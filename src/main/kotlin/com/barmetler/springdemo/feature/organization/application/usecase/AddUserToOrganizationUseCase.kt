package com.barmetler.springdemo.feature.organization.application.usecase

import com.barmetler.springdemo.feature.organization.domain.model.OrganizationUser
import com.barmetler.springdemo.feature.organization.infrastructure.repository.OrganizationRepository
import com.barmetler.springdemo.feature.user.infrastructure.repository.UserRepository
import com.barmetler.springdemo.security.permission.OrganizationPermission
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Transactional(readOnly = false)
class AddUserToOrganizationUseCase(
    @PersistenceContext
    private val em: EntityManager,

    private val userRepository: UserRepository,
    private val organizationRepository: OrganizationRepository,
) {
    /**
     * Add a user to a given organization.
     */
    @PreAuthorize(
        "hasPermission(#organizationId, T(${OrganizationPermission.CN}).ADD_USER)",
    )
    fun add(organizationId: UUID, userId: UUID) {
        // This approach results in a single INSERT query, as getting references does
        // not load them from the database, provided we only try to access their
        // primary identifiers subsequently.
        // The downside is that we do not get EntityNotFoundExceptions, the failure happens
        // during the insert as a FK-violation.
        val orgRef = organizationRepository.getReferenceById(organizationId)
        val userRef = userRepository.getReferenceById(userId)
        val ou = OrganizationUser(organization = orgRef, user = userRef)
        em.persist(ou)
        // TODO map remote key constraint exc into NotFound / AlreadyExists
    }
}
