package com.barmetler.springdemo

import com.barmetler.springdemo.user.api.dto.LoginRequest
import com.barmetler.springdemo.user.application.model.UserIdentifier
import com.barmetler.springdemo.user.application.usecase.CreateUserUseCase
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldEqual
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.not
import org.intellij.lang.annotations.Language
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper
import java.util.UUID
import kotlin.test.assertNotNull

@ApplyExtension(io.kotest.extensions.spring.SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [SpringDemoApplication::class])
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-integrationtest.properties")
@Transactional
class IntegrationTests @Autowired constructor(
    private val mvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val jwtDecoder: JwtDecoder,
    private val createUser: CreateUserUseCase,
) : StringSpec({
    val log = LoggerFactory.getLogger(IntegrationTests::class.java)

    "LoginUseCase should succeed with valid credentials" {
        val userId = createUser.create(email = "test@example.com", password = "password").id

        @Language("http-url-reference")
        val loginResponse = mvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                LoginRequest(
                    user = UserIdentifier.Email("test@example.com"),
                    password = "password",
                ),
            )
        }.andExpect {
            status { isOk() }
            cookie {
                exists("refresh-token")
                httpOnly("refresh-token", true)
                value("refresh-token", not(emptyString()))
            }
            content {
                jsonPath("sessionToken") {
                    exists()
                    value(not(emptyString()))
                }
            }
        }.andReturn().response

        val refreshCookie = assertNotNull(loginResponse.getCookie("refresh-token"))
        val refreshToken = refreshCookie.value
        val sessionToken = objectMapper.readTree(loginResponse.contentAsString)["sessionToken"].stringValue()

        log.info { "refreshToken: $refreshToken" }
        log.info { "sessionToken: $sessionToken" }

        val parsed = jwtDecoder.decode(sessionToken)
        val subject = UUID.fromString(parsed.subject)
        subject shouldEqual userId

        @Language("http-url-reference")
        val refreshResponse = mvc.post("/auth/refresh") {
            cookie(refreshCookie)
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("sessionToken") {
                    exists()
                    value(not(emptyString()))
                }
            }
        }.andReturn().response

        val sessionToken2 = objectMapper.readTree(refreshResponse.contentAsString)["sessionToken"].stringValue()

        log.info { "sessionToken2: $sessionToken2" }

        val parsed2 = jwtDecoder.decode(sessionToken)
        val subject2 = UUID.fromString(parsed2.subject)
        subject2 shouldEqual userId

        mvc.post(
            // language="http-url-reference"
            "/auth/logout",
        ) {
            cookie(refreshCookie)
        }.andExpect {
            status { isOk() }
            cookie {
                exists("refresh-token")
                httpOnly("refresh-token", true)
                value("refresh-token", emptyString())
                maxAge("refresh-token", 0)
            }
        }
    }
})
