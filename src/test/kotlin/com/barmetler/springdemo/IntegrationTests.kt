package com.barmetler.springdemo

import com.barmetler.springdemo.security.JwtParserService
import com.barmetler.springdemo.user.api.dto.LoginRequest
import com.barmetler.springdemo.user.api.dto.UserIdentifier
import com.barmetler.springdemo.user.usecases.CreateUserUseCase
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.StringSpec
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.not
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper
import kotlin.test.assertNotNull

@ApplyExtension(io.kotest.extensions.spring.SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [SpringDemoApplication::class])
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-integrationtest.properties")
@Transactional
class IntegrationTests @Autowired constructor(
    private val mvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val jwtParser: JwtParserService,
    private val createUser: CreateUserUseCase,
) : StringSpec({
    val log = LoggerFactory.getLogger(IntegrationTests::class.java)

    "LoginUseCase should succeed with valid credentials" {
        val userId = createUser.create(email = "test@example.com", password = "password").id

        @Language("http-url-reference")
        val loginResponse = mvc.post("/auth/login") {
            // language=
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

        val parsed = jwtParser.parse(sessionToken)
        val subject = parsed.userId
        Assertions.assertEquals(userId, subject)

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

        val parsed2 = jwtParser.parse(sessionToken2)
        val subject2 = parsed2.userId
        Assertions.assertEquals(userId, subject2)

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
