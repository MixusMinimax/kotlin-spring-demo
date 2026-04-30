package com.barmetler.springdemo.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SecurityException
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.time.Instant
import java.util.*


@Service
class JwtService(
    private val props: JwtProperties,
    @Qualifier("jwkKeyPair")
    private val keyPair: KeyPair
) {
    fun buildToken(
        extraClaims: MutableMap<String, Any?>?,
        userId: UUID
    ): String {
        val now = Instant.now()
        return Jwts
            .builder()
            .claims(extraClaims)
            .subject(userId.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(props.expirationTime)))
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
