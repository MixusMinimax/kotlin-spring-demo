package com.barmetler.springdemo.user.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "my_user")
class User(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,
)
