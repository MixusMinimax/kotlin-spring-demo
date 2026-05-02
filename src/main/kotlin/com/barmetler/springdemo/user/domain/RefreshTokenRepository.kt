package com.barmetler.springdemo.user.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, String> {
    @Modifying
    @Query(
        """
            delete from RefreshToken t
                where t.expiresAt < :asOf
                   or t.expiresAt < :asOf
        """
    )
    fun deleteAllInvalid(@Param("asOf") asOf: Instant)
}
