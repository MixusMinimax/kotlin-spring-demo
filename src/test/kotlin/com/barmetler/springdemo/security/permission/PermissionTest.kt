package com.barmetler.springdemo.security.permission

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.equals.shouldEqual
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.beInstanceOf

class PermissionTest : StringSpec({
    "map of permission companions must contain subclasses of Permission" {
        val map = Permission.createPermissionKindMap()
        map shouldNot beEmpty()
        map.entries.forAll { entry ->
            entry.key should beInstanceOf<String>()
            entry.value should beInstanceOf<Permission.PermissionCompanion<*>>()
        }
    }

    "valueOf should return correct permission kinds" {
        OrganizationPermission.ADD_USER shouldEqual Permission.valueOf(
            OrganizationPermission.targetKind,
            OrganizationPermission.ADD_USER.name,
        )
    }
})
