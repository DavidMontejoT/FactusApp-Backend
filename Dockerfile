# Multi-stage Dockerfile simplificado para FactusApp Backend
# Compila en Docker y copia el JAR correcto automáticamente

# Stage 1: Build - Compilar la aplicación
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app

# Copiar solo los archivos necesarios
COPY build.gradle ./
COPY src ./src

# Buildar (esto creará el Boot Jar con todos los recursos)
RUN gradle build -x test --no-daemon

# Listar archivos creados para debug
RUN ls -la build/libs/

# Stage 2: Runtime - Imagen final
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar cualquier JAR que se haya creado
RUN find /app/build/libs -name "*.jar" -type f -exec cp {} app.jar \;

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
