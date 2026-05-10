package com.barmetler.springdemo.user.application.usecase

import com.barmetler.springdemo.user.application.service.JwtFactory
import com.barmetler.springdemo.user.domain.model.RefreshToken
import com.barmetler.springdemo.user.infrastructure.persistence.RefreshTokenRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
@Transactional(
    isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED,
    readOnly = true,
)
class RefreshAccessTokenUseCase(
    private val jwtFactory: JwtFactory,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun refresh(tokenString: String): String {
        val token = refreshTokenRepository.findByIdOrNull(tokenString)
            ?: throw BadCredentialsException("invalid or missing refresh token")
        return refresh(token)
    }

    fun refresh(token: RefreshToken): String {
        val now = Instant.now()
        if (!token.isValid(now)) {
            throw CredentialsExpiredException("refresh token expired")
        }
        val user = token.user
        // TODO more claims, permissions, display name, profile picture url, etc.
        return jwtFactory.buildToken(user)
    }
}
