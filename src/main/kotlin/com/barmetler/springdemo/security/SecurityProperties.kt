package com.barmetler.springdemo.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource
import java.time.Duration

@ConfigurationProperties("security")
data class SecurityProperties(
    val jwt: JwtProperties,
    val refreshToken: RefreshTokenProperties = RefreshTokenProperties(),
) {
    data class JwtProperties(
        val jwk: JwkProperties,
        val expirationTime: Duration = Duration.ofMinutes(15),
        val secureCookie: Boolean = true,
    ) {
        data class JwkProperties(val publicKeyPath: Resource? = null, val privateKeyPath: Resource)
    }

    data class RefreshTokenProperties(val expirationTime: Duration = Duration.ofDays(60), val length: Int = 48)
}
