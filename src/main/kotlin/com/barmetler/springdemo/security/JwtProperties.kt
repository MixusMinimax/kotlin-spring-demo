package com.barmetler.springdemo.security

import org.springframework.core.io.Resource
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("security.jwt")
data class JwtProperties(
    val jwk: JwkProperties,
    val expirationTime: Duration = Duration.ofMinutes(15)
) {
    data class JwkProperties(
        val publicKeyPath: Resource? = null,
        val privateKeyPath: Resource,
    )
}



