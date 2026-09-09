# Этап 1: сборка и тесты (JDK и Gradle на хосте не нужны)
FROM gradle:8.13-jdk21 AS build
WORKDIR /app

# Сначала только файлы сборки — слой с зависимостями кешируется
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle --no-daemon dependencies --quiet > /dev/null || true

COPY src ./src
RUN gradle --no-daemon test bootJar

# Этап 2: лёгкий рантайм
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
