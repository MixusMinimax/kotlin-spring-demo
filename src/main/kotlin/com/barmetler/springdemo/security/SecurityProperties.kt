package com.barmetler.springdemo.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.core.io.Resource
import java.time.Duration

@ConfigurationProperties("security")
data class SecurityProperties(
    val jwt: JwtProperties,
    val refreshToken: RefreshTokenProperties = RefreshTokenProperties(),
) {
    data class JwtProperties(
        val jwk: JwkProperties,
        @DefaultValue("15m")
        val expirationTime: Duration = Duration.ofMinutes(15),
        val secureCookie: Boolean = true,
    ) {
        data class JwkProperties(
            val publicKeySetPath: Resource,
            val privateKeySetPath: Resource,
            val signingAlg: String,
            val signingKid: String? = null,
        )
    }

    data class RefreshTokenProperties(
        /**
         * The token can be used for generating access JWTs for this duration after creation.
         */
        @DefaultValue("60d")
        val expirationTime: Duration = Duration.ofDays(60),

        /**
         * Expired / revoked tokens are kept in the database for this duration for better error messages.
         */
        @DefaultValue("24h")
        val deleteAfter: Duration = Duration.ofHours(24),

        /**
         * The amount of bytes to generate before base 64 encoding.
         */
        val length: Int = 48,

        val cleanupCron: String = "0 0 2 * * *",
    )
}
