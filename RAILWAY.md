# Guía de Despliegue en Railway para Backend

## 🚀 Pasos para Desplegar en Railway

### 1. Preparar el Repositorio

Asegúrate de que tu repositorio de GitHub tenga:
- ✅ Código fuente del backend
- ✅ Archivo `build.gradle`
- ✅ Archivo `src/main/resources/application.yml`

### 2. Crear Cuenta en Railway

1. Ve a [https://railway.app/](https://railway.app/)
2. Regístrate con tu cuenta de GitHub
3. Verifica tu email

### 3. Crear Nuevo Proyecto

1. Click en **"New Project"**
2. Selecciona **"Deploy from GitHub repo"**
3. Busca y selecciona tu repositorio `factusapp-backend`
4. Click en **"Deploy Now"**

### 4. Configurar Base de Datos PostgreSQL

Railway detectará automáticamente que necesitas PostgreSQL y te pedirá agregarlo.

1. Click en **"New Service"**
2. Selecciona **"Database"**
3. Selecciona **"PostgreSQL"**
4. Railway creará la base de datos automáticamente

### 5. Configurar Variables de Entorno

En tu proyecto de Railway, ve a **"Variables"** y agrega:

```bash
# Configuración de Base de Datos (Railway lo hace automáticamente)
DATABASE_URL=${{Postgres.DATABASE_URL}}

# Configuración JWT
JWT_SECRET=FactusAppSuperSecretKeyForJWTTokenGenerationHS512MustBeAtLeast64CharactersLong1234567890ProductionMode
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Configuración Factus API
FACTUS_API_URL=https://api-sandbox.factus.com.co
FACTUS_CLIENT_ID=a11277dc-18d5-4f20-b216-4ce02dbe8407
FACTUS_CLIENT_SECRET=Qbl5tEw7DBCIMPRjMxsxkgximAOeThT6N6vfehzT
FACTUS_USERNAME=sandbox@factus.com.co
FACTUS_PASSWORD=sandbox2024%
FACTUS_DEMO_MODE=true

# Configuración CORS
CORS_ORIGINS=https://factusapp-demo.vercel.app,https://tu-dominio.vercel.app

# Configuración de Servidor
SERVER_PORT=8080
LOG_LEVEL=INFO
```

### 6. Configurar el Servicio Java

Railway necesita saber cómo ejecutar tu aplicación Java. Agrega un archivo `Procfile` en la raíz:

```
web: java -jar build/libs/*.jar
```

O en Railway, configura el **Root Directory** como vacío y el **Build Command** como:

```bash
./gradlew build -x test
```

Y el **Start Command** como:

```bash
java -jar build/libs/*.jar
```

### 7. Obtener la URL del Backend

Una vez desplegado, Railway te dará una URL como:
```
https://factusapp-backend-production.up.railway.app
```

Esta URL la necesitas para configurar el frontend.

### 8. Probar el Backend

Abre tu navegador y ve a:
```
https://factusapp-backend-production.up.railway.app/api/health
```

Deberías ver:
```json
{
  "version": "1.0.0",
  "message": "FactusApp backend funcionando correctamente",
  "timestamp": ...,
  "status": "UP"
}
```

## 🔧 Troubleshooting

### Error: "Could not find database driver"

Asegúrate de que en `build.gradle` tengas:
```gradle
implementation 'org.postgresql:postgresql:42.7.1'
runtimeOnly 'org.postgresql:postgresql'
```

### Error: "Connection refused"

- Verifica que la base de datos esté corriendo en Railway
- Espera unos minutos después de desplegar
- Revisa los logs en Railway

### Error: CORS en el frontend

Asegúrate de que `CORS_ORIGINS` incluya tu URL de Vercel.

## 📊 Monitoreo

En Railway puedes ver:
- **Logs**: Logs de la aplicación en tiempo real
- **Metrics**: CPU, memoria, disco
- **Deployments**: Historial de despliegues

## 💰 Costos

Railway tiene un plan gratuito:
- $5 USD de crédito gratuito cada mes
- Suficiente para desarrollo y pruebas
- Tarjeta de crédito requerida (no se cobra si estás en el plan gratuito)

## 🎯 Siguientes Pasos

1. ✅ Backend desplegado en Railway
2. ✅ Copiar la URL del backend
3. ✅ Configurar esa URL en Vercel (frontend)
4. ✅ Probar la integración completa
