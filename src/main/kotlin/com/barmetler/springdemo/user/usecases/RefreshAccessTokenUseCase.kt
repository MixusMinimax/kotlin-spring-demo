package com.barmetler.springdemo.user.usecases

import com.barmetler.springdemo.user.domain.RefreshToken
import com.barmetler.springdemo.user.domain.RefreshTokenRepository
import com.barmetler.springdemo.user.services.JwtGeneratorService
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
    private val jwtGenerator: JwtGeneratorService,
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
        // TODO: more claims, permissions, display name, profile picture url, etc.
        return jwtGenerator.buildToken(user)
    }
}
