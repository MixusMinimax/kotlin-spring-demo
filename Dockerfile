# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper
RUN chmod +x gradlew
# download gradle
RUN ./gradlew --version --no-daemon

COPY gradle/libs.versions.toml gradle/libs.versions.toml
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

RUN ./gradlew dependencies --no-daemon || true

COPY src src

RUN ./gradlew clean bootJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
