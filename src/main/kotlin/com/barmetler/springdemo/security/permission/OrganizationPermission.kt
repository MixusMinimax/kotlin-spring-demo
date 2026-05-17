package com.barmetler.springdemo.security.permission

import org.intellij.lang.annotations.Language

enum class OrganizationPermission : Permission {
    ADD_USER,
    ;

    override val targetKind: String = "organization"

    override fun toAuthorityString(target: Any) = "$targetKind:$target:$name"

    companion object {
        @Language("jvm-class-name")
        const val CN = "com.barmetler.springdemo.security.permission.OrganizationPermission"
    }
}
