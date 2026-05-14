package com.barmetler.springdemo.feature.organization.domain.model

import com.barmetler.springdemo.feature.user.domain.model.User
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import org.hibernate.annotations.BatchSize
import java.time.Instant

@Entity
class OrganizationUser(
    @EmbeddedId
    var id: OrganizationUserId,

    @ManyToOne(fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    @MapsId("organizationId")
    var organization: Organization,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    var user: User,

    var createdOn: Instant = Instant.now(),

    // I can add permissions and roles here
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrganizationUser) return false
        return id == other.id
    }

    final override fun hashCode(): Int = id.hashCode()
}
