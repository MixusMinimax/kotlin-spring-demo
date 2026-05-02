package com.barmetler.springdemo.security

import io.jsonwebtoken.security.Jwks
import io.jsonwebtoken.security.PrivateJwk
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.SecurityContextHolderFilter
import java.security.KeyPair

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

    @Bean
    fun createTraceRepository(): HttpExchangeRepository = InMemoryHttpExchangeRepository()
}
