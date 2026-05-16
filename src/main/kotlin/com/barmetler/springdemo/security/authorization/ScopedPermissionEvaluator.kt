package com.barmetler.springdemo.security.authorization

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
        val required = permission as? Permission ?: return false
        val targetIdString = targetDomainObject.toString()
        val expectedAuthority = "${required.targetKind}:$targetIdString:${required.name}"
        return authentication.authorities.any { it.authority == expectedAuthority }
    }

    override fun hasPermission(
        authentication: Authentication,
        targetId: Serializable,
        targetType: String,
        permission: Any,
    ): Boolean {
        val required = permission as? Permission ?: return false
        if (targetType != required.targetKind) return false
        val targetIdString = targetId.toString()
        val expectedAuthority = "$targetType:$targetIdString:${required.name}"
        return authentication.authorities.any { it.authority == expectedAuthority }
    }
}
