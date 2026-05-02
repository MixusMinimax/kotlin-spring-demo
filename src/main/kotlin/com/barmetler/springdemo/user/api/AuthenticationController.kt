package com.barmetler.springdemo.user.api

import com.barmetler.springdemo.user.api.dto.LoginRequest
import com.barmetler.springdemo.user.usecases.RefreshAccessTokenUseCase
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val refreshAccessTokenUseCase: RefreshAccessTokenUseCase,
) {
    val logger: Logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    @GetMapping("/refresh")
    fun refresh(@CookieValue("refresh-token") tokenString: String?, response: HttpServletResponse) {
        if (tokenString == null) {
            throw BadCredentialsException("not logged in.")
        }
        val jwt = refreshAccessTokenUseCase.refresh(tokenString)
        val cookie = Cookie("refresh-token", jwt).apply {
//            secure = true
            isHttpOnly = true
        }
        response.addCookie(cookie)
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): String {
        return req.toString()
    }
}
