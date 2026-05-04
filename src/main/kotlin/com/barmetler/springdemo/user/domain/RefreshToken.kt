package com.barmetler.springdemo.user.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
class RefreshToken(
    @Id
    @Column(length = 255)
    var token: String,

    @Column(nullable = false)
    var expiresAt: Instant,

    var revokedAt: Instant? = null,

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(updatable = false, nullable = false)
    var user: User,
) {
    fun isValid(asOf: Instant) = expiresAt.isAfter(asOf) && revokedAt?.isAfter(asOf) ?: true
}
