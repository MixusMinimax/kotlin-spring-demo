package com.barmetler.springdemo.security

import com.barmetler.springdemo.security.permission.Permission
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
class ScopedPermissionEvaluator : PermissionEvaluator {
    override fun hasPermission(
        authentication: Authentication,
        targetDomainObject: Any,
        permission: Any,
    ): Boolean {
        if (permission !is Permission) return false
        return hasPermission(authentication, targetDomainObject, permission)
    }

    override fun hasPermission(
        authentication: Authentication,
        targetId: Serializable,
        targetType: String,
        permission: Any,
    ): Boolean {
        if (permission !is Permission) return false
        if (targetType != permission.targetKind) return false
        return hasPermission(authentication, targetId, permission)
    }

    private fun hasPermission(authentication: Authentication, target: Any, permission: Permission): Boolean {
        val expectedAuthority = permission.toAuthorityString(target)
        return authentication.authorities.any {
            it.authority.equals(expectedAuthority, ignoreCase = true)
        }
    }
}
