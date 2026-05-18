package com.barmetler.springdemo.feature.user.infrastructure.repository

import com.barmetler.springdemo.feature.user.application.model.UserIdentifier
import com.barmetler.springdemo.feature.user.domain.model.User
import com.barmetler.springdemo.feature.user.domain.model.User_
import org.springframework.data.jpa.domain.PredicateSpecification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    fun findById(filter: UserIdentifier): User? = findOne { from, builder ->
        when (filter) {
            is UserIdentifier.Email -> builder.equal(from.get(User_.email), filter.email)
            is UserIdentifier.Id -> builder.equal(from.get(User_.id), filter.id)
        }
    }.orElse(null)

    // I would love it if I could keep the original findOne, but add a new function that has the annotation.
    // sadly, the annotation only works on functions that have no default implementation, and functions without
    // an implementation can't accept Specification or PredicateSpecification, it is ignored.
    @EntityGraph(attributePaths = ["organizations.permissions"], type = EntityGraph.EntityGraphType.LOAD)
    override fun findOne(spec: PredicateSpecification<User>): Optional<User>
}
