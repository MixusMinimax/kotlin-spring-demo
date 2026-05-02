package com.barmetler.springdemo.user.usecases

import com.barmetler.springdemo.security.JwtService
import com.barmetler.springdemo.security.SecurityProperties
import com.barmetler.springdemo.user.api.dto.UserIdentifier
import com.barmetler.springdemo.user.domain.RefreshToken
import com.barmetler.springdemo.user.domain.RefreshTokenRepository
import com.barmetler.springdemo.user.domain.User
import com.barmetler.springdemo.user.domain.User_
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaQuery
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import java.security.SecureRandom
import java.time.Instant
import kotlin.io.encoding.Base64
import kotlin.random.asKotlinRandom

@Component
@Transactional(
    isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED,
    readOnly = false,
)
class LoginUseCase(
    private val props: SecurityProperties,
    @PersistenceContext
    private val em: EntityManager,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {
    fun login(id: UserIdentifier, password: String): String {
        val user = em.createQuery(id.toQuery(em)).singleResult
        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw BadCredentialsException("Invalid Password.")
        }
        val random = SecureRandom().asKotlinRandom()
        val tokenString = Base64.encode(random.nextBytes(48))
        val now = Instant.now()
        val token = refreshTokenRepository.save(
            RefreshToken(
                token = tokenString,
                expiresAt = now.plus(props.refreshToken.expirationTime),
                user = user,
            )
        )
        Assert.state(tokenString == token.token) { "token field does not equal tokenString." }
        return token.token
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
