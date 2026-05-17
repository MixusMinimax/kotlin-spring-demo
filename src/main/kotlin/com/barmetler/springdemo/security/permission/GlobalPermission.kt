package com.barmetler.springdemo.security.permission

import org.intellij.lang.annotations.Language

enum class GlobalPermission : Permission {
    CREATE_ORGANIZATION,
    ;

    override val targetKind get() = GlobalPermission.targetKind

    override fun toAuthorityString(target: Any): String = "$targetKind:$name"

    companion object : Permission.PermissionCompanion<GlobalPermission> {
        @Language("jvm-class-name")
        const val CN = "com.barmetler.springdemo.security.permission.GlobalPermission"

        override val targetKind = "global"
        override fun valueOf(value: String) = GlobalPermission.valueOf(value)
    }
}
