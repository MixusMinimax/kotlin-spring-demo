package com.barmetler.springdemo.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
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
        jwtDecoder: JwtDecoder,
        jwtAuthenticationConverter: Converter<Jwt, AbstractAuthenticationToken>,
    ): SecurityFilterChain = http
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .formLogin { it.disable() }
        .httpBasic { it.disable() }
        .oauth2ResourceServer {
            it.jwt { jwt ->
                jwt.decoder(jwtDecoder)
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
            }
        }
        .requestCache { it.disable() }
        .authorizeHttpRequests {
            it
                .requestMatchers("/auth/login", "/auth/logout", "/auth/refresh").permitAll()
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                ).permitAll()
                .requestMatchers("/actuator/**").permitAll() // will be removed later
                .anyRequest().authenticated()
        }
        .build()

    @Bean
    fun jwtDecoder(props: SecurityProperties): JwtDecoder {
        val json = (props.jwt.jwk.publicKeySetPath ?: props.jwt.jwk.privateKeySetPath).inputStream.bufferedReader()
            .use { it.readText() }
        val jwkSet = JWKSet.parse(json).toPublicJWKSet()
        val jwkSource = ImmutableJWKSet<SecurityContext>(jwkSet)
        return NimbusJwtDecoder(MyJwtProcessor(jwkSource))
    }

    @Bean
    fun jwtEncoder(props: SecurityProperties): JwtEncoder {
        val json = props.jwt.jwk.privateKeySetPath.inputStream.bufferedReader().use { it.readText() }
        val jwkSet = JWKSet.parse(json)
        val jwkSource = ImmutableJWKSet<SecurityContext>(jwkSet)
        return MyJwtEncoder(jwkSource)
    }

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        val converter = JwtAuthenticationConverter()

        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val roles = jwt.getClaimAsStringList("roles") ?: emptyList()
            roles.map { SimpleGrantedAuthority("ROLE_$it") }
        }

        return Converter {
            val auth = converter.convert(it) as JwtAuthenticationToken
            // TODO maybe wrap jwt
            JwtAuthenticationToken(auth.token, auth.authorities, auth.name)
        }
    }

    @Bean(defaultCandidate = false)
    fun secureRandom(): Random = SecureRandom()

    @Bean
    @Profile("dev")
    fun createTraceRepository(): HttpExchangeRepository = InMemoryHttpExchangeRepository()
}
