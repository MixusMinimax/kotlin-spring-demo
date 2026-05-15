package com.barmetler.springdemo.feature.organization.application.usecase

import com.barmetler.springdemo.feature.organization.domain.model.OrganizationUser
import com.barmetler.springdemo.feature.organization.infrastructure.repository.OrganizationRepository
import com.barmetler.springdemo.feature.user.infrastructure.repository.UserRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.cache.spi.access.AccessType
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
    fun add(organizationId: UUID, userId: UUID) {
        AccessType.READ_ONLY
        // This approach results in a single INSERT query, as getting references does
        // not load them from the database, provided we only try to access their
        // primary identifiers subsequently.
        // The downside is that we do not get EntityNotFoundExceptions, the failure happens
        // during the insert as a FK-violation.
        val orgRef = organizationRepository.getReferenceById(organizationId)
        val userRef = userRepository.getReferenceById(userId)
        val ou = OrganizationUser(organization = orgRef, user = userRef)
        em.persist(ou)
        // TODO map remote key constraint exc into NotFound
    }
}
