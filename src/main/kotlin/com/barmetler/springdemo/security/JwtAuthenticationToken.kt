package com.barmetler.springdemo.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwt
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import java.util.UUID

class JwtAuthenticationToken(
    val serialized: String,
    val jwt: Jwt<out Header, out Claims>,
    authorities: Collection<GrantedAuthority>,
) : AbstractAuthenticationToken(authorities) {

    val userId: UUID = UUID.fromString(jwt.payload.subject)

    override fun getCredentials(): Jwt<out Header, out Claims> = jwt

    override fun getPrincipal(): UUID = userId
}
