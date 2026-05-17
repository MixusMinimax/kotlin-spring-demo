package com.barmetler.springdemo

import com.barmetler.springdemo.security.permission.OrganizationPermission
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.Session
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
@Profile("demo-data")
@Transactional
class DemoInit(
    @PersistenceContext
    private val entityManager: EntityManager,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {
    private val logger = KotlinLogging.logger { }

    override fun run(vararg args: String) {
        val session = entityManager.unwrap(Session::class.java)

        val userId = UUID.fromString("f7266604-9eb0-4c69-a48f-a36dc309c7e8")
        val orgId = UUID.fromString("16195aa4-e234-4aa1-8430-62b65851705c")

        session.createNativeMutationQuery(
            """
                INSERT INTO my_user(id, email, password_hash)
                VALUES (:user_id, :email, :password_hash);
                
                INSERT INTO organization(id, slug, name)
                VALUES (:org_id, :slug, :name);
                
                INSERT INTO organization_user(organization_id, user_id, created_on)
                VALUES (:org_id, :user_id, NOW());
                
                INSERT INTO organization_user_permissions(
                    organization_user_organization_id, organization_user_user_id, permissions)
                VALUES (:org_id, :user_id, :perm);
            """,
        )
            .setParameter("user_id", userId)
            .setParameter("email", "demo@example.com")
            .setParameter("password_hash", passwordEncoder.encode("password123"))
            .setParameter("org_id", orgId)
            .setParameter("slug", "demo-organization")
            .setParameter("name", "Demo Organization")
            .setParameter("perm", OrganizationPermission.ADD_USER.name)
            .executeUpdate()

        logger.info { "created demo user with id $userId, and demo org with id $orgId, added permission ADD_USER" }
    }
}
