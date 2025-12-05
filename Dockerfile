# build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

COPY src src

# ⛔ saltamos los tests
RUN ./gradlew --no-daemon clean build -x test

# run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

CMD ["java", "-jar", "/app/app.jar"]
