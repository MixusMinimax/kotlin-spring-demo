package com.barmetler.springdemo.feature.organization.application.mapper

import com.barmetler.springdemo.feature.organization.application.model.OrganizationRecord
import com.barmetler.springdemo.feature.organization.domain.model.Organization

fun Organization.toOrganizationRecord() = OrganizationRecord(
    id = checkNotNull(id),
    slug = slug,
    name = name,
)
