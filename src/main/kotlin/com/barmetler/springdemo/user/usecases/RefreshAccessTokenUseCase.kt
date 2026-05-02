package com.barmetler.springdemo.user.usecases

import com.barmetler.springdemo.security.JwtService
import com.barmetler.springdemo.user.domain.RefreshTokenRepository
import com.barmetler.springdemo.user.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(
    isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED,
    readOnly = true,
)
class RefreshAccessTokenUseCase(
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun refresh(tokenString: String): String {
        val token = refreshTokenRepository.findByIdOrNull(tokenString)
            ?: throw BadCredentialsException("invalid refresh token")
        val user = token.user
        // TODO: more claims, permissions, display name, profile picture url, etc.
        return jwtService.buildToken(user.id!!)
    }
}
