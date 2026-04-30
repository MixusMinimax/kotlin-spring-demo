package com.barmetler.springdemo.security

import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG
import io.jsonwebtoken.impl.security.DefaultOctetPrivateJwk
import io.jsonwebtoken.security.EcPrivateJwk
import io.jsonwebtoken.security.Jwks
import io.jsonwebtoken.security.PrivateJwk
import io.jsonwebtoken.security.RsaPrivateJwk
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.SecurityContextHolderFilter
import java.security.KeyPair
import java.security.PrivateKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.EdECPrivateKey
import java.time.Instant
import java.util.Date
import kotlin.jvm.Throws

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtFilter: JwtAuthenticationFilter,
        authenticationProvider: JwtAuthenticationProvider,
    ): SecurityFilterChain = http
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .formLogin { it.disable() }
        .httpBasic { it.disable() }
        .requestCache { it.disable() }
        .authorizeHttpRequests {
            it
                .requestMatchers("/auth/login", "/auth/logout", "/auth/refresh").permitAll()
                .anyRequest().authenticated()
        }
        .addFilterBefore(jwtFilter, SecurityContextHolderFilter::class.java)
        .authenticationProvider(authenticationProvider)
        .build()

    @Bean(defaultCandidate = false)
    @Throws
    fun jwkKeyPair(props: JwtProperties): KeyPair {
        val json = props.jwk.privateKeyPath.inputStream.bufferedReader().use { it.readText() }
        val jwk = Jwks.parser().build().parse(json) as PrivateJwk<*, *, *>
        return jwk.toKeyPair().toJavaKeyPair()
    }
}
