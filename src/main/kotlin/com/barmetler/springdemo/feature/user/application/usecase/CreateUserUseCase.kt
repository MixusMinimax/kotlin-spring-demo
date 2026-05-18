package com.barmetler.springdemo.feature.user.application.usecase

import com.barmetler.springdemo.feature.user.application.mapper.toUserRecord
import com.barmetler.springdemo.feature.user.application.model.UserRecord
import com.barmetler.springdemo.feature.user.domain.model.User
import com.barmetler.springdemo.feature.user.infrastructure.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(
    isolation = Isolation.READ_COMMITTED,
    propagation = Propagation.REQUIRED,
    readOnly = false,
)
class CreateUserUseCase(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
) {
    fun create(email: String, password: String): UserRecord {
        val user = User(
            email = email,
            passwordHash = requireNotNull(passwordEncoder.encode(password)),
        )
        return userRepository.save(user).toUserRecord()
    }
}
