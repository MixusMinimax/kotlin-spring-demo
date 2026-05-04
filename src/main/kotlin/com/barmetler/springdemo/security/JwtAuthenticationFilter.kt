package com.barmetler.springdemo.security

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtParser: JwtParserService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (SecurityContextHolder.getContext().authentication == null) {
            val authorizationHeader: String? = request.getHeader("Authorization")
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                val token = authorizationHeader.substringAfter("Bearer ")
                try {
                    val auth = jwtParser.parse(token)
                    SecurityContextHolder.getContext().authentication = auth
                } catch (ex: JwtException) {
                    throw BadCredentialsException(ex.message, ex)
                }
            }
        }

        return filterChain.doFilter(request, response)
    }
}
