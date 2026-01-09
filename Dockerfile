# Build:
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar

# Runtime:
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]