package com.barmetler.springdemo.feature.organization.infrastructure.repository

import com.barmetler.springdemo.feature.organization.domain.model.Organization
import com.barmetler.springdemo.feature.organization.domain.model.Organization_
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.Session
import org.hibernate.query.Order
import org.hibernate.query.Page
import org.springframework.stereotype.Repository

@Repository
class OrganizationRepositoryCustomImpl(
    @PersistenceContext
    private val em: EntityManager,
) : OrganizationRepositoryCustom {
    override fun findAllBySlugPaginated(slugAfter: String?, maxCount: Int?): List<Organization> {
        Page.page(1, 2).keyedBy(Order.asc(Organization_.slug))
        val session = em.unwrap(Session::class.java)
        val cb = session.criteriaBuilder
        val q = cb.createQuery(Organization::class.java)
        val organization = q.from(Organization::class.java)
        q.select(organization)
        slugAfter?.let { q.where(session.criteriaBuilder.greaterThan(organization.get(Organization_.slug), it)) }
        q.orderBy(cb.asc(organization.get(Organization_.slug)))
        val hq = session.createQuery(q)
        maxCount?.let { hq.maxResults = it }
        return hq.resultList
    }
}
