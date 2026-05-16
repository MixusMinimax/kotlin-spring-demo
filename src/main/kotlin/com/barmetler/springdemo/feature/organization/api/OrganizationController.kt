package com.barmetler.springdemo.feature.organization.api

import com.barmetler.springdemo.feature.organization.application.model.PaginatedOrganizationRecordList
import com.barmetler.springdemo.feature.organization.application.usecase.AddUserToOrganizationUseCase
import com.barmetler.springdemo.feature.organization.application.usecase.CreateOrganizationUseCase
import com.barmetler.springdemo.feature.organization.application.usecase.ListOrganizationsUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/organizations")
class OrganizationController(
    private val addUserToOrganization: AddUserToOrganizationUseCase,
    private val createOrganization: CreateOrganizationUseCase,
    private val listOrganizations: ListOrganizationsUseCase,
) {
    @GetMapping
    fun list(
        @RequestParam slugAfter: String? = null,
        @RequestParam maxCount: Int? = null,
    ): PaginatedOrganizationRecordList = listOrganizations.list(slugAfter = slugAfter, maxCount = maxCount)
}
