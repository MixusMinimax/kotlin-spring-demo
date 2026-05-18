import dev.detekt.gradle.Detekt
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.kapt)

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependencyManagement)

    alias(libs.plugins.hibernate)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

group = "com.barmetler"
version = "0.1.0-SNAPSHOT"
description = "spring-demo"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

configurations {
    testImplementation {
        exclude("org.mockito", "mockito-core")
        exclude("org.mockito", "mockito-junit-jupiter")
    }
}

dependencies {
    // Spring Boot starters
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.dataJpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2ResourceServer)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.liquibase)
    implementation(libs.spring.boot.starter.actuator)
    annotationProcessor(libs.spring.boot.configurationProcessor)

//    // Blaze Persistence
//    implementation(platform(libs.blaze.bom))
//    implementation(libs.blaze.core.api)
//    runtimeOnly(libs.blaze.core.impl)
//    runtimeOnly(libs.blaze.integration.hibernate)

    // crypt
    runtimeOnly(libs.google.tink)

    // Database
    runtimeOnly(libs.postgresql)

    // Kotlin
    implementation(libs.kotlin.reflect)

    // Jackson Kotlin module
    implementation(libs.jackson.kotlin)

    // Logging
    implementation(libs.kotlin.logging)

    // Dev tools
    implementation(libs.springdoc.ui)
    developmentOnly(libs.spring.boot.devtools)

    kapt(libs.kapt.hibernateProcessor)

    // Tests
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.dataJpa.test)
    testImplementation(libs.spring.boot.starter.security.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.h2)
    testRuntimeOnly(libs.junit.platformLauncher)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.extension.spring)

    testImplementation(libs.springmockk)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.bootJar {
    layered {
        enabled.set(true)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED", "--sun-misc-unsafe-memory-access=allow")
}

ktlint {
    version = "1.8.0"
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(file("config/detekt/detekt.yml"))
}

tasks.withType<Detekt> {
    reports {
        sarif.required = true
    }
}
