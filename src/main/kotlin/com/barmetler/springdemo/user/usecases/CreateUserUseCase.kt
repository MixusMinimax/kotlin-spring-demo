package com.barmetler.springdemo.user.usecases

import com.barmetler.springdemo.user.api.dto.UserDTO
import com.barmetler.springdemo.user.domain.User
import com.barmetler.springdemo.user.domain.UserRepository
import com.barmetler.springdemo.user.api.dto.toUserDTO
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
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun create(email: String, password: String): UserDTO {
        val user = User(
            email = email,
            passwordHash = requireNotNull(passwordEncoder.encode(password))
        )
        return userRepository.save(user).toUserDTO()
    }
}
