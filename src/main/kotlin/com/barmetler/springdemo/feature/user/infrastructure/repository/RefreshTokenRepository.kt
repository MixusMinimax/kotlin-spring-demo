package com.barmetler.springdemo.feature.user.infrastructure.repository

import com.barmetler.springdemo.feature.user.domain.model.RefreshToken
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {

    @EntityGraph(attributePaths = ["user.organizations.permissions"], type = EntityGraph.EntityGraphType.LOAD)
    override fun findById(id: String): Optional<RefreshToken>

    @Modifying
    @Query(
        """
            delete from RefreshToken t
                where t.expiresAt < :asOf
                   or t.expiresAt < :asOf
        """,
    )
    fun deleteAllInvalid(@Param("asOf") asOf: Instant)
}
