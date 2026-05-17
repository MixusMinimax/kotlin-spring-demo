package com.barmetler.springdemo.security.permission

import org.intellij.lang.annotations.Language

enum class GlobalPermission : Permission {
    CREATE_ORGANIZATION,
    ;

    override val targetKind = "global"

    override fun toAuthorityString(target: Any): String = "$targetKind:$name"

    companion object {
        @Language("jvm-class-name")
        const val CN = "com.barmetler.springdemo.security.permission.GlobalPermission"
    }
}
