# ==============================================================================
# ESTÁGIO 1: Build da Aplicação (Multi-stage - Referência caso compile do código-fonte)
# Caso o código-fonte (src/ e pom.xml) esteja presente no diretório, descomente:
# ==============================================================================
# FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
# WORKDIR /build
# COPY pom.xml .
# RUN mvn dependency:go-offline -B
# COPY src ./src
# RUN mvn clean package -DskipTests

# ==============================================================================
# ESTÁGIO 2: Runtime enxuto (JRE 17)
# Executa o artefato JAR lendo as variáveis de ambiente injetadas pelo Kubernetes
# ==============================================================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Variáveis de ambiente padrão (sobrescritas em tempo de execução pelo ConfigMap/Secret no K8s)
ENV DB_HOST=mysql-service \
    DB_PORT=3306 \
    DB_NAME=blackcat \
    DB_USER=blackcat \
    DB_PASSWORD=blackcat123 \
    JAVA_TOOL_OPTIONS="-XX:+UseSerialGC -Xms128m -Xmx384m"

# Copia o artefato compilado da aplicação Spring Boot
# (Se utilizar o Estágio 1 acima, altere para: COPY --from=builder /build/target/*.jar app.jar)
COPY blackcat.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]