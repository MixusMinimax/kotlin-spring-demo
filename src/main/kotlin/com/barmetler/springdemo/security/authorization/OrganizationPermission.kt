package com.barmetler.springdemo.security.authorization

enum class OrganizationPermission : Permission {
    ADD_USER,
    ;

    override val targetKind: String = "organization"
}
