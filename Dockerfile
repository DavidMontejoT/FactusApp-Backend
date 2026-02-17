# Dockerfile para FactusApp Backend
# Multi-stage build para optimizar tamaño

# Stage 1: Build
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app

# Copiar archivos de configuración
COPY build.gradle settings.gradle ./
COPY src ./src

# Buildar la aplicación
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime
FROM openjdk:17-slim
WORKDIR /app

# Instalar dependencias necesarias
RUN apt-get update && apt-get install -y \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# Copiar JAR del stage de build
COPY --from=build /app/build/libs/*.jar app.jar

# Crear directorio para logs
RUN mkdir -p /app/logs

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
