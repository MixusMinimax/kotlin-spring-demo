package com.barmetler.springdemo.security.permission

import org.intellij.lang.annotations.Language

enum class OrganizationPermission : Permission {
    ADD_USER,
    ;

    override val targetKind: String get() = OrganizationPermission.targetKind

    override fun toAuthorityString(target: Any) = "$targetKind:$target:$name"

    companion object : Permission.PermissionCompanion<OrganizationPermission> {
        @Language("jvm-class-name")
        const val CN = "com.barmetler.springdemo.security.permission.OrganizationPermission"

        override val targetKind = "organization"
        override fun valueOf(value: String) = OrganizationPermission.valueOf(value)
    }
}
