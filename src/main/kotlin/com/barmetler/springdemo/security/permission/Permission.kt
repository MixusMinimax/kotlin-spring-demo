package com.barmetler.springdemo.security.permission

interface Permission {
    val targetKind: String
    val name: String

    fun toAuthorityString(target: Any): String
}
