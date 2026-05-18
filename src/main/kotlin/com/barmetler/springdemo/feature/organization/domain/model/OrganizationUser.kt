package com.barmetler.springdemo.feature.organization.domain.model

import com.barmetler.springdemo.feature.user.domain.model.User
import com.barmetler.springdemo.security.permission.OrganizationPermission
import jakarta.persistence.ElementCollection
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import java.time.Instant

@Entity
class OrganizationUser(
    @EmbeddedId
    var id: OrganizationUserId,

    @ManyToOne(fetch = FetchType.LAZY)
//    @BatchSize(size = 50) // this ain't working
    @MapsId("organizationId")
    var organization: Organization,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    var user: User,

    var createdOn: Instant = Instant.now(),

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    var permissions: MutableSet<OrganizationPermission> = HashSet(),
) {
    constructor(organization: Organization, user: User) : this(
        id = OrganizationUserId(
            organizationId = organization.id!!,
            userId = user.id!!,
        ),
        organization = organization,
        user = user,
    )

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OrganizationUser) return false
        return id == other.id
    }

    final override fun hashCode(): Int = id.hashCode()
}
