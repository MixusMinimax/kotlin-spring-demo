package com.barmetler.springdemo.security

import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.security.PublicKey

@Service
class JwtParserService(
    @Qualifier("jwkPublicKey")
    val publicKey: PublicKey,
) {
    @Throws(JwtException::class)
    fun parse(token: String): JwtAuthenticationToken {
        val jwt = Jwts.parser()
            .verifyWith(publicKey)
            .build().parse(token).accept(Jws.CLAIMS)
        return JwtAuthenticationToken(serialized = token, jwt = jwt, authorities = listOf())
    }
}
