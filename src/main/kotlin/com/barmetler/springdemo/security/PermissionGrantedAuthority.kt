package com.barmetler.springdemo.security

import com.barmetler.springdemo.security.permission.Permission
import org.springframework.security.core.GrantedAuthority

data class PermissionGrantedAuthority<out T : Any, out P : Permission>(
    val target: T,
    val permission: P,
    val authorityString: String,
) : GrantedAuthority {
    constructor(
        target: T,
        permission: P,
    ) : this(
        target = target,
        permission = permission,
        authorityString = permission.toAuthorityString(target),
    )

    override fun getAuthority() = authorityString
}
