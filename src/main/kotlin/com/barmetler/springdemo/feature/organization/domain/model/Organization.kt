package com.barmetler.springdemo.feature.organization.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.NaturalId
import org.hibernate.annotations.NaturalIdCache
import java.util.UUID

@Entity
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Organization(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    /**
     * Business key with max length is 255.
     *
     * Should follow `kebab-case`.
     */
    @Column(length = 255)
    @NaturalId(mutable = true)
    var slug: String,

    /**
     * Human-readable name.
     */
    @Column(nullable = false)
    var name: String,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Organization) return false
        return slug == other.slug
    }

    final override fun hashCode(): Int = slug.hashCode()
}
