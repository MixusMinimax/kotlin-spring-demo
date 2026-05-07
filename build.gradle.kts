plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.kapt)

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependencyManagement)

    alias(libs.plugins.hibernate)
    alias(libs.plugins.kotest)
}

group = "com.barmetler"
version = "0.0.1-SNAPSHOT"
description = "spring-demo"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
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
    implementation(libs.spring.boot.starter.dataJpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.liquibase)
    implementation(libs.spring.boot.starter.actuator)
    annotationProcessor(libs.spring.boot.configurationProcessor)

    // Database
    runtimeOnly(libs.postgresql)

    // Kotlin
    implementation(libs.kotlin.reflect)

    // Jackson Kotlin module
    implementation(libs.jackson.kotlin)

    // JJWT
    implementation(libs.jjwt.api)
    implementation(libs.jjwt.impl)
    implementation(libs.jjwt.jackson)

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

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED")
}
