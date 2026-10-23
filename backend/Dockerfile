# ==============================================================================
# ESTÁGIO 1: Build da Aplicação (Maven + JDK 17)
# Compila o código-fonte do Backend e gera o artefato executável (.jar)
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copia o pom.xml primeiro para aproveitar o cache de camadas do Docker nas dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e empacota a aplicação (ignorando testes no build de container)
COPY src ./src
RUN mvn clean package -DskipTests

# ==============================================================================
# ESTÁGIO 2: Runtime Enxuto (JRE 17)
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

# Copia apenas o JAR compilado no Estágio 1
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
