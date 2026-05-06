package com.barmetler.springdemo.user.usecases

import com.barmetler.springdemo.security.SecurityProperties
import com.barmetler.springdemo.user.api.dto.UserIdentifier
import com.barmetler.springdemo.user.domain.RefreshToken
import com.barmetler.springdemo.user.domain.RefreshTokenRepository
import com.barmetler.springdemo.user.domain.User
import com.barmetler.springdemo.user.domain.User_
import com.barmetler.springdemo.user.services.JwtGeneratorService
import com.barmetler.springdemo.user.services.RefreshTokenGeneratorService
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaQuery
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import java.security.SecureRandom
import java.time.Instant
import java.util.Random
import kotlin.io.encoding.Base64
import kotlin.random.asKotlinRandom

@Component
@Transactional(
    isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED,
    readOnly = false,
)
class LoginUseCase(
    private val passwordEncoder: PasswordEncoder,
    private val em: EntityManager,
    private val refreshTokenGenerator: RefreshTokenGeneratorService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtGenerator: JwtGeneratorService,
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
        val user = em.createQuery(id.toQuery(em)).singleResult
        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw BadCredentialsException("Invalid Password.")
        }
        val token = refreshTokenRepository.save(refreshTokenGenerator.generate(user))
        val sessionToken = jwtGenerator.buildToken(user)
        return LoginResult(
            refreshToken = token.token,
            sessionToken = sessionToken,
        )
    }

    // I'll leave this here for demonstration purposes. But for kotlin, maybe just normal jpa queries are better,
    // since the metamodel generator is made for java, so it requires kapt.
    // Kapt is kind of being phased out, or not, who knows, they can't seem to make up their mind.
    private fun UserIdentifier.toQuery(em: EntityManager): CriteriaQuery<User> {
        val builder = em.criteriaBuilder
        val query = builder.createQuery(User::class.java)
        val root = query.from(User::class.java)
        val where =
            when (this) {
                is UserIdentifier.Email -> builder.equal(root.get(User_.email), email)
                is UserIdentifier.Id -> builder.equal(root.get(User_.id), id)
            }
        return query.select(root).where(where)
    }
}
