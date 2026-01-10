# Build:
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . /app

# Executa os testes unitários e de integração primeiro
# Se algum teste falhar, o build do Docker será interrompido aqui
RUN ./gradlew test --no-daemon

# Gera o JAR apenas se os testes passarem
RUN ./gradlew bootJar --no-daemon

# Runtime:
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# Adicionamos a flag de agente também no runtime caso sua app
# use bibliotecas que façam proxy em tempo de execução (como Hibernate/Spring)
ENTRYPOINT ["java", "-XX:+EnableDynamicAgentLoading", "-jar", "app.jar"]