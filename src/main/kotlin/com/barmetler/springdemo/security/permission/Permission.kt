package com.barmetler.springdemo.security.permission

import kotlin.reflect.full.companionObjectInstance

sealed interface Permission {
    val targetKind: String
    val name: String

    fun toAuthorityString(target: Any): String

    interface PermissionCompanion<Self : Permission> {
        val targetKind: String
        fun valueOf(value: String): Self
    }

    companion object {
        fun createPermissionKindMap(): Map<String, PermissionCompanion<*>> {
            val permissionClass = Permission::class
            return permissionClass.sealedSubclasses.asSequence()
                .map { it.companionObjectInstance }
                .filterIsInstance<PermissionCompanion<*>>().associateBy { it.targetKind }
        }

        val permissionKindMap by lazy { createPermissionKindMap() }

        fun valueOf(kind: String, value: String) = permissionKindMap.getValue(kind).valueOf(value)
    }
}
