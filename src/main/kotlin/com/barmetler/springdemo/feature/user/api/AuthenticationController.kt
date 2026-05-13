package com.barmetler.springdemo.feature.user.api

import com.barmetler.springdemo.feature.user.api.dto.LoginRequest
import com.barmetler.springdemo.feature.user.api.dto.LoginResponse
import com.barmetler.springdemo.feature.user.api.dto.RefreshResponse
import com.barmetler.springdemo.feature.user.application.usecase.LoginUseCase
import com.barmetler.springdemo.feature.user.application.usecase.RefreshAccessTokenUseCase
import com.barmetler.springdemo.security.SecurityProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val props: SecurityProperties,
    private val refreshAccessTokenUseCase: RefreshAccessTokenUseCase,
    private val loginUseCase: LoginUseCase,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/refresh")
    fun refresh(@CookieValue("refresh-token") tokenString: String?): RefreshResponse {
        if (tokenString == null) {
            throw BadCredentialsException("not logged in.")
        }
        val jwt = refreshAccessTokenUseCase.refresh(tokenString)
        return RefreshResponse(sessionToken = jwt)
    }

    @PostMapping("/login")
    fun login(
        @CookieValue("refresh-token") tokenString: String?,
        @RequestBody req: LoginRequest,
        response: HttpServletResponse,
    ): LoginResponse {
        val res = loginUseCase.login(id = req.user, password = req.password, previousRefreshToken = tokenString)
        val cookie = Cookie("refresh-token", res.refreshToken).apply {
            secure = props.jwt.secureCookie
            isHttpOnly = true
            path = "/"
        }
        response.addCookie(cookie)
        return LoginResponse(res.sessionToken)
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): String {
        val cookie = Cookie("refresh-token", "").apply {
            secure = props.jwt.secureCookie
            isHttpOnly = true
            path = "/"
            maxAge = 0
        }
        response.addCookie(cookie)
        return "logged out."
    }

    @GetMapping("/info")
    fun info(principal: Principal): String {
        logger.info { principal }
        return principal.name
    }
}
