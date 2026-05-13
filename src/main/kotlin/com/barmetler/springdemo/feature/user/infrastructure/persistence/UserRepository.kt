package com.barmetler.springdemo.feature.user.infrastructure.persistence

import com.barmetler.springdemo.feature.user.application.model.UserIdentifier
import com.barmetler.springdemo.feature.user.domain.model.User
import com.barmetler.springdemo.feature.user.domain.model.User_
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    fun findById(filter: UserIdentifier): User? = findOne { from, builder ->
        when (filter) {
            is UserIdentifier.Email -> builder.equal(from.get(User_.email), filter.email)
            is UserIdentifier.Id -> builder.equal(from.get(User_.id), filter.id)
        }
    }.orElse(null)
}
