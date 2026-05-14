package com.barmetler.springdemo.feature.user.domain.model

import com.barmetler.springdemo.feature.organization.domain.model.OrganizationUser
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "my_user")
class User(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var organizations: MutableSet<OrganizationUser> = HashSet(),
)
