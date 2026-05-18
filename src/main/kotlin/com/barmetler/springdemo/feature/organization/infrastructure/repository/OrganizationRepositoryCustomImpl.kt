package com.barmetler.springdemo.feature.organization.infrastructure.repository

import com.barmetler.springdemo.feature.organization.domain.model.Organization
import com.barmetler.springdemo.feature.organization.domain.model.Organization_
import com.barmetler.springdemo.feature.organization.infrastructure.repository.OrganizationRepositoryCustom.PaginatedOrganizationList
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.Session
import org.springframework.stereotype.Repository

@Repository
class OrganizationRepositoryCustomImpl(
    @PersistenceContext
    private val em: EntityManager,
) : OrganizationRepositoryCustom {
    override fun findAllPaginated(slugAfter: String?, maxCount: Int?): PaginatedOrganizationList {
        val session = em.unwrap(Session::class.java)
        val cb = session.criteriaBuilder
        val q = cb.createQuery(Organization::class.java)
        val organization = q.from(Organization::class.java)
        q.select(organization)
        slugAfter?.let { q.where(session.criteriaBuilder.greaterThan(organization.get(Organization_.slug), it)) }
        q.orderBy(cb.asc(organization.get(Organization_.slug)))
        val hq = session.createQuery(q)

        // Query one more item to determine whether we have reached the end
        maxCount?.let { hq.maxResults = it + 1 }

        // Execute query
        val results = hq.resultList

        val hasMore = maxCount != null && results.size > maxCount
        return if (hasMore) {
            PaginatedOrganizationList(
                // Do not return the extra item
                organizations = results.subList(0, maxCount),
                hasMore = true,
                lastSlugOfPage = results[maxCount - 1].slug,
            )
        } else {
            PaginatedOrganizationList(
                organizations = results,
                hasMore = false,
                lastSlugOfPage = null,
            )
        }
    }
}
