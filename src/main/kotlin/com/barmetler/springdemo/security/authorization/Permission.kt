package com.barmetler.springdemo.security.authorization

interface Permission {
    val targetKind: String
    val name: String
}
