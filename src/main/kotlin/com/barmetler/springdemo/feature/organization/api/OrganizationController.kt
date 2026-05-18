package com.barmetler.springdemo.feature.organization.api

import com.barmetler.springdemo.feature.organization.api.dto.AddUserToOrganizationRequest
import com.barmetler.springdemo.feature.organization.api.dto.CreateOrganizationRequest
import com.barmetler.springdemo.feature.organization.application.model.OrganizationRecord
import com.barmetler.springdemo.feature.organization.application.model.PaginatedOrganizationRecordList
import com.barmetler.springdemo.feature.organization.application.usecase.AddUserToOrganizationUseCase
import com.barmetler.springdemo.feature.organization.application.usecase.CreateOrganizationUseCase
import com.barmetler.springdemo.feature.organization.application.usecase.ListOrganizationsUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/organizations")
class OrganizationController(
    private val listOrganizations: ListOrganizationsUseCase,
    private val createOrganization: CreateOrganizationUseCase,
    private val addUserToOrganization: AddUserToOrganizationUseCase,
) {
    @GetMapping
    fun list(
        @RequestParam slugAfter: String? = null,
        @RequestParam maxCount: Int? = null,
    ): PaginatedOrganizationRecordList = listOrganizations.list(
        slugAfter = slugAfter,
        maxCount = maxCount,
    )

    @PostMapping("/new")
    fun create(
        @RequestBody req: CreateOrganizationRequest,
    ): OrganizationRecord = createOrganization.create(
        slug = req.slug,
        name = req.name,
    )

    @PostMapping("/add-user")
    fun addUser(
        @RequestBody req: AddUserToOrganizationRequest,
    ) = addUserToOrganization.add(
        organizationId = req.organizationId,
        userId = req.userId,
    )
}
