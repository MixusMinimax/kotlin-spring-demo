package com.barmetler.springdemo.user.application.service

import com.barmetler.springdemo.security.SecurityProperties
import com.barmetler.springdemo.user.domain.model.RefreshToken
import com.barmetler.springdemo.user.domain.model.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Random
import kotlin.io.encoding.Base64
import kotlin.random.asKotlinRandom

@Component
class RefreshTokenFactory(
    private val props: SecurityProperties,
    @Qualifier("secureRandom")
    private val random: Random,
) {
    fun generate(user: User): RefreshToken {
        val random = random.asKotlinRandom()
        val tokenString = Base64.encode(random.nextBytes(props.refreshToken.length))
        val now = Instant.now()

        return RefreshToken(
            token = tokenString,
            expiresAt = now.plus(props.refreshToken.expirationTime),
            user = user,
        )
    }
}
