package com.barmetler.springdemo.user.usecases

import com.barmetler.springdemo.user.api.dto.UserIdentifier
import com.barmetler.springdemo.user.domain.RefreshToken
import com.barmetler.springdemo.user.domain.RefreshTokenRepository
import com.barmetler.springdemo.user.domain.User
import com.barmetler.springdemo.user.services.JwtGeneratorService
import com.barmetler.springdemo.user.services.RefreshTokenGeneratorService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldEqual
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.util.*

class LoginUseCaseTest : StringSpec({
    val criteriaBuilder = mockk<CriteriaBuilder>(relaxed = true)
    val query = mockk<TypedQuery<User>>()
    val em = mockk<EntityManager>()
    val refreshTokenGenerator = mockk<RefreshTokenGeneratorService>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val jwtGenerator = mockk<JwtGeneratorService>()

    val fakeUser = User(
        id = UUID.fromString("35e96262-cd0a-4aa8-b6bc-6bc3a0a15514"),
        email = "user@example.com",
        passwordHash = "hashed",
    )

    val fakeToken = RefreshToken(
        token = "fake-token",
        expiresAt = Instant.now(),
        revokedAt = null,
        user = fakeUser,
    )

    val sut = LoginUseCase(
        passwordEncoder = passwordEncoder,
        em = em,
        refreshTokenGenerator = refreshTokenGenerator,
        refreshTokenRepository = refreshTokenRepository,
        jwtGenerator = jwtGenerator,
    )

    "login should return correct tokens with valid credentials" {
        every { refreshTokenGenerator.generate(any()) } returns fakeToken

        every { em.criteriaBuilder } returns criteriaBuilder
        every { em.createQuery(any<CriteriaQuery<User>>()) } returns query
        every { query.singleResult } returns fakeUser

        every { passwordEncoder.matches("pw", "hashed") } returns true

        every { refreshTokenRepository.deleteById(any()) } just Runs
        every { refreshTokenRepository.save(any()) } answers { firstArg() }

        every { jwtGenerator.buildToken(fakeUser) } returns "jwt"

        val result = sut.login(
            id = UserIdentifier.Email(fakeUser.email),
            password = "pw",
            previousRefreshToken = "old",
        )

        result.sessionToken shouldEqual "jwt"
        result.refreshToken shouldEqual fakeToken.token
    }
})
