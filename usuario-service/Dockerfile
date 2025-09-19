# Stage 1: build
FROM gradle:8.5-jdk21-jammy AS builder

USER root

# Atualiza certificados e instala ferramentas de rede
RUN apt-get update && \
    apt-get install -y ca-certificates curl wget && \
    rm -rf /var/lib/apt/lists/* && \
    update-ca-certificates

WORKDIR /app

# Copia todos os arquivos primeiro
COPY . .

# Usa o gradle da imagem ao invés do wrapper para evitar downloads
RUN gradle clean build -x test --no-daemon \
    --refresh-dependencies \
    --stacktrace \
    -Dorg.gradle.internal.http.connectionTimeout=60000 \
    -Dorg.gradle.internal.http.socketTimeout=60000 \
    -Djava.net.useSystemProxies=true

# Stage 2: runtime
FROM openjdk:21
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]