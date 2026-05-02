package com.barmetler.springdemo.security

import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.time.Instant
import java.util.*


@Service
class JwtService(
    private val props: SecurityProperties,
    @Qualifier("jwkKeyPair")
    private val keyPair: KeyPair
) {
    fun buildToken(
        userId: UUID,
        extraClaims: MutableMap<String, Any?>? = null,
    ): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(userId.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(props.jwt.expirationTime)))
            .signWith(keyPair.private)
            .compact()
    }

    @Throws(JwtException::class)
    fun parse(token: String): JwtAuthenticationToken {
        val jwt = Jwts.parser()
            .verifyWith(keyPair.public)
            .build().parse(token).accept(Jws.CLAIMS)
        return JwtAuthenticationToken(serialized = token, jwt = jwt, authorities = listOf())
    }
}
