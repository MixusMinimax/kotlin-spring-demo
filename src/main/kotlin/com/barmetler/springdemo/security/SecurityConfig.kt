package com.barmetler.springdemo.security

import io.jsonwebtoken.security.Jwks
import io.jsonwebtoken.security.PrivateJwk
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.SecurityContextHolderFilter
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.util.Random

@Configuration
@EnableConfigurationProperties(SecurityProperties::class)
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
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**"
                ).permitAll()
                .requestMatchers("/actuator/**").permitAll() // will be removed later
                .anyRequest().authenticated()
        }
        .addFilterBefore(jwtFilter, SecurityContextHolderFilter::class.java)
        .authenticationProvider(authenticationProvider)
        .build()

    @Bean(defaultCandidate = false)
    @Throws
    fun jwkKeyPair(props: SecurityProperties): KeyPair {
        val json = props.jwt.jwk.privateKeyPath.inputStream.bufferedReader().use { it.readText() }
        val jwk = Jwks.parser().build().parse(json) as PrivateJwk<*, *, *>
        return jwk.toKeyPair().toJavaKeyPair()
    }

    @Bean(defaultCandidate = false)
    fun jwkPublicKey(@Qualifier("jwkKeyPair") keyPair: KeyPair): PublicKey = keyPair.public

    @Bean(defaultCandidate = false)
    fun jwkPrivateKey(@Qualifier("jwkKeyPair") keyPair: KeyPair): PrivateKey = keyPair.private

    @Bean(defaultCandidate = false)
    fun secureRandom(): Random = SecureRandom()

    @Bean
    @Profile("dev")
    fun createTraceRepository(): HttpExchangeRepository = InMemoryHttpExchangeRepository()
}
