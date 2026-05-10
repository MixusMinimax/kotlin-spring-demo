package com.barmetler.springdemo.user.services

import com.barmetler.springdemo.security.SecurityProperties
import com.barmetler.springdemo.user.domain.RefreshToken
import com.barmetler.springdemo.user.domain.RefreshTokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RefreshTokenCleanupService(
    private val props: SecurityProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    /**
     * Deletes all [RefreshToken]s that have become invalid by at least 24h.
     *
     * A token is considered invalid if it has either expired or been revoked before the cutoff.
     *
     * Tokens are being kept around for some time to allow for better error handling in attempted logins.
     */
    @Scheduled(cron = $$"${security.refresh-token.cleanup-cron}")
    fun cleanupRefreshTokens() {
        refreshTokenRepository.deleteAllInvalid(Instant.now().minus(props.refreshToken.deleteAfter))
    }
}
