package com.barmetler.springdemo.user.application.usecase

import com.barmetler.springdemo.user.application.model.UserIdentifier
import com.barmetler.springdemo.user.application.service.JwtFactory
import com.barmetler.springdemo.user.application.service.RefreshTokenFactory
import com.barmetler.springdemo.user.domain.model.RefreshToken
import com.barmetler.springdemo.user.infrastructure.persistence.RefreshTokenRepository
import com.barmetler.springdemo.user.infrastructure.persistence.UserRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(
    isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED,
    readOnly = false,
)
class LoginUseCase(
    private val passwordEncoder: PasswordEncoder,
    private val jwtFactory: JwtFactory,
    private val refreshTokenFactory: RefreshTokenFactory,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
) {
    data class LoginResult(
        val refreshToken: String,
        val sessionToken: String,
    )

    /**
     * Checks the [password] for the user selected by [id] using the application context's [PasswordEncoder].
     *
     * If provided, the [RefreshToken] identified by [previousRefreshToken] is deleted.
     * If this token does not exist, it is silently ignored.
     *
     * A random [RefreshToken] is generated with the configured expiry settings.
     *
     * A session token is generated.
     *
     * @param id identifies the user by either an email address or id (more in the future).
     * @param password raw password string.
     * @param previousRefreshToken the token string of the existing token. May be null.
     */
    fun login(
        id: UserIdentifier,
        password: String,
        previousRefreshToken: String? = null,
    ): LoginResult {
        previousRefreshToken?.let { refreshTokenRepository.deleteById(it) }
        val user = userRepository.findById(id) ?: throw BadCredentialsException("Invalid user.")
        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw BadCredentialsException("Invalid Password.")
        }
        val token = refreshTokenRepository.save(refreshTokenFactory.generate(user))
        val sessionToken = jwtFactory.buildToken(user)
        return LoginResult(
            refreshToken = token.token,
            sessionToken = sessionToken,
        )
    }
}
