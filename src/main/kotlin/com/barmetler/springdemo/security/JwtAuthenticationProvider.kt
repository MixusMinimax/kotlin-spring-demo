package com.barmetler.springdemo.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import kotlin.reflect.full.isSubclassOf

@Component
class JwtAuthenticationProvider : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {
        authentication.isAuthenticated = true
        return authentication
    }

    override fun supports(authentication: Class<*>): Boolean =
        authentication.kotlin.isSubclassOf(JwtAuthenticationToken::class)
}
