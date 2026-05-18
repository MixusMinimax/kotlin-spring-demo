package com.barmetler.springdemo.feature.user.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant

@Entity
class RefreshToken(
    @Id
    @Column(length = 255)
    var token: String,

    @Column(nullable = false)
    var expiresAt: Instant,

    var revokedAt: Instant? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(updatable = false, nullable = false)
    var user: User,
) {
    fun isValid(asOf: Instant) = expiresAt.isAfter(asOf) && revokedAt?.isAfter(asOf) ?: true
}
