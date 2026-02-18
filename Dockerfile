# Multi-stage Dockerfile para FactusApp Backend
# Compila el proyecto en Docker para evitar problemas con archivos locales

# Stage 1: Build - Compilar la aplicación
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app

# Copiar solo los archivos necesarios (sin settings.gradle que no existe)
COPY build.gradle ./
COPY src ./src

# Buildar
RUN gradle build -x test --no-daemon

# Stage 2: Runtime - Imagen final pequeña
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar el Boot Jar específico (incluye todos los recursos)
COPY --from=build /app/build/libs/factusapp-backend-1.0.0.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080
ENV PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
