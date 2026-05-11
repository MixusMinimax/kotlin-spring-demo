package com.barmetler.springdemo.user.usecases

import com.barmetler.springdemo.user.application.model.UserIdentifier
import com.barmetler.springdemo.user.application.service.JwtFactory
import com.barmetler.springdemo.user.application.service.RefreshTokenFactory
import com.barmetler.springdemo.user.application.usecase.LoginUseCase
import com.barmetler.springdemo.user.domain.model.RefreshToken
import com.barmetler.springdemo.user.domain.model.User
import com.barmetler.springdemo.user.infrastructure.persistence.RefreshTokenRepository
import com.barmetler.springdemo.user.infrastructure.persistence.UserRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldEqual
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.util.UUID

class LoginUseCaseTest : StringSpec({
    val refreshTokenFactory = mockk<RefreshTokenFactory>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val jwtFactory = mockk<JwtFactory>()
    val userRepository = mockk<UserRepository>()

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
        jwtFactory = jwtFactory,
        refreshTokenFactory = refreshTokenFactory,
        refreshTokenRepository = refreshTokenRepository,
        userRepository = userRepository,
    )

    "login should return correct tokens with valid credentials" {
        every { refreshTokenFactory.generate(any()) } returns fakeToken

        every { passwordEncoder.matches("pw", "hashed") } returns true

        every { refreshTokenRepository.deleteById(any()) } just Runs
        every { refreshTokenRepository.save(any()) } answers { firstArg() }

        every { jwtFactory.buildToken(fakeUser) } returns "jwt"

        every { userRepository.findById(any<UserIdentifier>()) } returns fakeUser

        val result = sut.login(
            id = UserIdentifier.Email(fakeUser.email),
            password = "pw",
            previousRefreshToken = "old",
        )

        result.sessionToken shouldEqual "jwt"
        result.refreshToken shouldEqual fakeToken.token
    }
})
