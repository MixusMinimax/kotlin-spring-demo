package com.barmetler.springdemo.feature.user.application.service

import com.barmetler.springdemo.feature.user.domain.model.User
import com.barmetler.springdemo.security.SecurityProperties
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtFactory(
    private val props: SecurityProperties,
    private val encoder: JwtEncoder,
) {
    fun buildToken(
        user: User,
        extraClaims: MutableMap<String, Any>? = null,
    ): String {
        val now = Instant.now()
        return encoder.encode(
            JwtEncoderParameters.from(
                JwsHeader.with { props.jwt.jwk.signingAlg }
                    .let { b -> props.jwt.jwk.signingKid?.let { b.keyId(it) } ?: b }
                    .build(),
                JwtClaimsSet.builder()
                    .claims { extraClaims?.let(it::putAll) }
                    .subject(user.id!!.toString())
                    .issuedAt(now)
                    .expiresAt(now.plus(props.jwt.expirationTime))
                    .build(),
            ),
        ).tokenValue
    }
}
