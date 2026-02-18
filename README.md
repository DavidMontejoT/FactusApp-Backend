# FactusApp Backend 🇨🇴

Sistema backend para facturación electrónica integrado con **Factus API** y **DIAN**.

## 🌐 Producción

**Frontend en vivo:** [https://factusapp-frontend.onrender.com](https://factusapp-frontend.onrender.com)

**Backend API:** https://factusapp-backend-1.onrender.com/api

**Health Check:** https://factusapp-backend-1.onrender.com/api/health

**Estado:** ✅ Activo y funcionando

---

## 🚀 Aplicación en Producción

**Credenciales de prueba:**
- Email: `test@test.com`
- Password: `escribeme +573013188696`

---

## ✨ Características

- ✅ API REST completa con Spring Boot 3.2.0
- ✅ Autenticación JWT con access y refresh tokens
- ✅ Autorización basada en roles
- ✅ Integración con Factus API (sandbox)
- ✅ Emisión de facturas a DIAN
- ✅ Base de datos PostgreSQL con Flyway
- ✅ Documentación OpenAPI/Swagger
- ✅ CORS configurado para producción
- ✅ Soporte para planes (FREE, BASIC, FULL)
- ✅ Modo demo para pruebas sin API real

---

## 🛠️ Stack Tecnológico

### Core
- **Java 17** - Lenguaje
- **Spring Boot 3.2.0** - Framework
- **Maven** - Gestión de dependencias
- **Gradle 8.5** - Build tool

### Persistencia
- **PostgreSQL 16** - Base de datos
- **Spring Data JPA** - ORM
- **Hibernate** - JPA Provider
- **Flyway** - Migraciones

### Seguridad
- **Spring Security** - Seguridad
- **JJWT 0.11.5** - JWT tokens
- **BCrypt** - Encriptación de passwords
- **CORS** - Configurado para frontend en Render

### Documentación
- **SpringDoc OpenAPI** - Swagger UI
- **Swagger** - Documentación API

---

## 📦 Estructura del Proyecto

```
factusapp-backend/
├── src/main/java/com/factusapp/
│   ├── controller/          # Controladores REST
│   │   ├── AuthController.java
│   │   ├── ClientController.java
│   │   ├── DashboardController.java
│   │   ├── InvoiceController.java
│   │   └── ProductController.java
│   ├── service/             # Lógica de negocio
│   │   ├── AuthService.java
│   │   ├── ClientService.java
│   │   ├── DashboardService.java
│   │   ├── FactusService.java
│   │   ├── InvoiceService.java
│   │   └── ProductService.java
│   ├── model/               # Entidades JPA
│   │   ├── User.java
│   │   ├── Client.java
│   │   ├── Product.java
│   │   ├── Invoice.java
│   │   └── InvoiceItem.java
│   ├── repository/          # Repositorios Spring Data JPA
│   ├── dto/                 # Objetos de transferencia
│   ├── security/            # Configuración JWT
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtAuthenticationEntryPoint.java
│   └── config/             # Configuración general
│       ├── SecurityConfig.java
│       ├── WebConfig.java (CORS)
│       └── RestTemplateConfig.java
└── resources/
    ├── application.yml      # Configuración principal
    └── db/migration/         # Migraciones Flyway
        ├── V1__create_tables.sql
        └── V2__add_dian_fields.sql
```

---

## 🔌 Endpoints API

### Autenticación
```
POST /api/auth/login       - Iniciar sesión
POST /api/auth/register    - Registro de usuarios
POST /api/auth/refresh     - Refresh token
```

### Dashboard
```
GET /api/dashboard/stats           - Estadísticas generales
GET /api/dashboard/recent-invoices - Facturas recientes
```

### Clientes
```
GET    /api/clients             - Listar todos los clientes
GET    /api/clients/{id}        - Obtener un cliente
POST   /api/clients             - Crear cliente
PUT    /api/clients/{id}        - Actualizar cliente
DELETE /api/clients/{id}        - Eliminar cliente
GET    /api/clients/search?term= - Buscar clientes
```

### Productos
```
GET    /api/products             - Listar todos los productos
GET    /api/products/{id}        - Obtener un producto
POST   /api/products             - Crear producto
PUT    /api/products/{id}        - Actualizar producto
DELETE /api/products/{id}        - Eliminar producto
GET    /api/products/low-stock   - Productos con stock bajo
GET    /api/products/out-of-stock - Productos agotados
GET    /api/products/search?term= - Buscar productos
```

### Facturas
```
GET    /api/invoices             - Listar todas las facturas
GET    /api/invoices/{id}        - Obtener una factura
POST   /api/invoices             - Crear factura
PUT    /api/invoices/{id}        - Actualizar factura
DELETE /api/invoices/{id}        - Eliminar factura
POST   /api/invoices/{id}/emit  - Emitir a DIAN (modo demo/producción)
GET    /api/invoices/{id}/xml   - Descargar XML
GET    /api/invoices/{id}/pdf   - Descargar PDF
```

### Health
```
GET /api/health - Health check del servidor
```

---

## 🔧 Configuración

### Variables de Entorno

```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
PGUSER=usuario
PGPASSWORD=password
PGDATABASE=factusapp

# JWT
JWT_SECRET=tu_clave_secreta_min_64_caracteres
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Factus API (Sandbox)
FACTUS_API_URL=https://api-sandbox.factus.com.co
FACTUS_CLIENT_ID=tu_client_id
FACTUS_CLIENT_SECRET=tu_client_secret
FACTUS_USERNAME=tu_usuario_factus
FACTUS_PASSWORD=tu_password_factus
FACTUS_DEMO_MODE=true

# Nota: Las credenciales de sandbox se obtienen en https://sandbox.factus.com.co

# CORS
CORS_ORIGINS=https://factusapp-frontend.onrender.com,https://*.onrender.com

# Servidor
SERVER_PORT=8080
LOG_LEVEL=INFO
```

---

## 🏗️ Build y Ejecución Local

### Prerrequisitos
- Java 17+
- PostgreSQL 16
- Gradle 8.5+

### Ejecutar

```bash
# Clonar repositorio
git clone https://github.com/DavidMontejoT/FactusApp-Backend.git

# Entrar al directorio
cd FactusApp-Backend

# Construir proyecto
./gradlew build

# Ejecutar
./gradlew bootRun
```

El backend estará disponible en `http://localhost:8080`

### Swagger UI

Una vez iniciado, accede a:
```
http://localhost:8080/swagger-ui.html
```

---

## 📊 Planes de Usuario

### Plan FREE
- 15 facturas/mes
- 20 productos
- 30 clientes
- Dashboard básico
- **Sin emisión DIAN** (solo modo demo)

### Plan BASIC ($45.000 COP/mes)
- 50 facturas/mes
- 100 productos
- 200 clientes
- Integración DIAN

### Plan FULL ($99.000 COP/mes)
- Facturas ilimitadas
- Productos ilimitados
- Clientes ilimitados
- Integración DIAN completa
- Reportes avanzados

---

## 🔐 Seguridad

### Autenticación
- JWT (HS512) con access y refresh tokens
- Access token: 15 minutos
- Refresh token: 7 días
- Refresh automático de tokens

### Autorización
- Roles: USER, ADMIN
- Los usuarios solo pueden acceder a sus propios recursos
- Validación de ownership en todos los endpoints

### Headers de Seguridad
```bash
# Authorization header
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

# Content-Type
Content-Type: application/json
```

---

## 🌐 Despliegue en Producción

### Plataforma
**Render.com** - Free tier

### URL de Producción
- **Backend API:** https://factusapp-backend-1.onrender.com/api
- **Frontend:** https://factusapp-frontend.onrender.com
- **Swagger:** https://factusapp-backend-1.onrender.com/swagger-ui.html

### Base de Datos
- PostgreSQL 16 en Render
- Conexión interna (segura)
- Backups automáticos

### Arquitectura
```
┌─────────────────┐
│  Render DNS      │
└────────┬────────┘
         │
    ┌────┴────┬────────────┐
    │         │            │
┌───┴───┐  ┌───┴────┐  ┌────▼─────┐
│ Front│  │ Backend│  │PostgreSQL│
│      │  │        │  │          │
└──────┘  └────────┘  └──────────┘
```

---

## 🧪 Testing

### Ejemplo: Login

```bash
curl -X POST https://factusapp-backend-1.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@test.com",
    "password": "Password123!"
  }'
```

### Ejemplo: Health Check

```bash
curl https://factusapp-backend-1.onrender.com/api/health
```

Respuesta esperada:
```json
{
  "version": "1.0.0",
  "message": "FactusApp backend funcionando correctamente",
  "status": "UP"
}
```

---

## 📝 Notas Técnicas

### Modo Demo vs Producción

**Modo Demo** (actual):
- `FACTUS_DEMO_MODE=true`
- Simula llamadas a Factus API
- No requiere credenciales reales
- Ideal para desarrollo y pruebas

**Modo Producción:**
- `FACTUS_DEMO_MODE=false`
- Usa credenciales reales de Factus API
- Facturas con validez legal en Colombia
- Requiere plan BASIC o FULL

### Flyway Migraciones

Las migraciones se ejecutan automáticamente al iniciar:
- `V1__create_tables.sql` - Tablas principales
- `V2__add_dian_fields.sql` - Campos para DIAN

### Validaciones

- Jakarta Bean Validation (@Valid, @NotNull, @Email, etc.)
- Validaciones personalizadas en servicios
- Manejo de excepciones global

---

## 🐛 Troubleshooting

### Error 401 Unauthorized
- Verifica que el token JWT esté en el header `Authorization`
- El token puede haber expirado (15 min)
- Usa el endpoint `/api/auth/refresh` para obtener un nuevo token

### Error 403 Forbidden
- Verifica que el recurso te pertenezca (ownership validation)
- Los roles son USER por defecto, no ADMIN

### Error de conexión a BD
- Verifica que PostgreSQL esté corriendo
- Revisa las variables de entorno PG*
- Verifica la cadena de conexión

---

## 📚 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Factus API Docs](https://api-docs.factus.com.co/)
- [JWT.io](https://jwt.io/)

---

## 👨‍💻 Desarrollo

### Autor
**David Montejo** - Reto API Factus 2026

### Año
2026

### Licencia
MIT License - Uso libre para fines educativos

---

## 🎯 Reto API Factus 2026

Este proyecto fue desarrollado para participar en el **Reto API Factus 2026** convocado por HALLTEC.

### Objetivo del Reto
Integrar la API de Factus para permitir la emisión de facturas electrónicas válidas en Colombia, cumpliendo con los estándares de la DIAN (Dirección de Impuestos y Aduanas Nacionales).

### Logros Alcanzados
✅ Integración completa con Factus API sandbox
✅ Emisión de facturas en modo demo
✅ CRUD completo de clientes, productos y facturas
✅ Autenticación y autorización JWT
✅ Despliegue en producción en Render
✅ Documentación completa

---

**🚀 FactusApp Backend - Listo para producción!**

*Para Colombia 🇨🇴, con ❤️*
