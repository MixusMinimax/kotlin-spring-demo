package com.barmetler.springdemo.feature.user.infrastructure.scheduler

import com.barmetler.springdemo.feature.user.application.usecase.CleanupRefreshTokensUseCase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CleanupRefreshTokensJob(private val cleanupRefreshTokensUseCase: CleanupRefreshTokensUseCase) {
    @Scheduled(cron = $$"${security.refresh-token.cleanup-cron}")
    fun runCleanup() {
        cleanupRefreshTokensUseCase.cleanupRefreshTokens()
    }
}
