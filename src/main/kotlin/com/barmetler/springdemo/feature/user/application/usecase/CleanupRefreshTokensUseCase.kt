package com.barmetler.springdemo.feature.user.application.usecase

import com.barmetler.springdemo.feature.user.domain.model.RefreshToken
import com.barmetler.springdemo.feature.user.infrastructure.repository.RefreshTokenRepository
import com.barmetler.springdemo.security.SecurityProperties
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
@Transactional(readOnly = false)
class CleanupRefreshTokensUseCase(
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
    fun cleanupRefreshTokens() {
        refreshTokenRepository.deleteAllInvalid(Instant.now().minus(props.refreshToken.deleteAfter))
    }
}
