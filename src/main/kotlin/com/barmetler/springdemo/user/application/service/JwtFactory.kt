package com.barmetler.springdemo.user.application.service

import com.barmetler.springdemo.security.SecurityProperties
import com.barmetler.springdemo.user.domain.model.User
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.security.PrivateKey
import java.time.Instant
import java.util.Date

@Component
class JwtFactory(
    private val props: SecurityProperties,
    @Qualifier("jwkPrivateKey")
    private val privateKey: PrivateKey,
) {
    fun buildToken(
        user: User,
        extraClaims: MutableMap<String, Any?>? = null,
    ): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(user.id!!.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(props.jwt.expirationTime)))
            .signWith(privateKey)
            .compact()
    }
}
