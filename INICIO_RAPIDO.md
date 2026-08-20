# 🐟 Sistema de Gestión de Pescadería - Inicio Rápido

## ⚡ Configuración en 5 Pasos

### 1️⃣ Instalar PostgreSQL

```bash
# Ver MIGRACION.md para instrucciones detalladas por sistema operativo
```

### 2️⃣ Crear Base de Datos

```sql
CREATE DATABASE pescaderia_db;
```

### 3️⃣ Configurar Conexión

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA
```

### 4️⃣ Recargar Dependencias

```bash
# Windows
gradlew.bat clean build --refresh-dependencies

# Linux/Mac
./gradlew clean build --refresh-dependencies
```

### 5️⃣ Ejecutar

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

## 📱 Acceder a la API

- **Aplicación**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Ejemplos HTTP**: Ver `api-examples.http`

## 🎯 Flujo Básico de Uso

```
1. Registrar Usuario → 2. Login → 3. Crear Productos →
4. Registrar Compra → 5. Crear Preparación → 6. Vender
```

### Ejemplo Rápido con cURL

```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Admin","correo":"admin@pescaderia.com","contrasena":"admin123","rolId":1}'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@pescaderia.com","contrasena":"admin123"}'

# Guardar el token que devuelve y usarlo en las siguientes peticiones:
# -H "Authorization: Bearer TU_TOKEN_AQUI"
```

## 📚 Documentación Completa

- **README Principal**: `README_PESCADERIA.md`
- **Guía Migración**: `MIGRACION.md`
- **Ejemplos API**: `api-examples.http`
- **Schema SQL**: `src/main/resources/db/schema.sql`

## 🆘 Problemas Comunes

| Problema       | Solución                                       |
| -------------- | ---------------------------------------------- |
| No compila     | `./gradlew clean build --refresh-dependencies` |
| No conecta DB  | Verificar PostgreSQL corriendo y credenciales  |
| Puerto ocupado | Cambiar `server.port` en properties            |
| Errores JPA    | Reload proyecto en IDE                         |

## ✨ Características del Sistema

✅ Gestión de compras de pescado  
✅ Procesamiento/preparación (fileteado)  
✅ Control automático de inventario  
✅ Ventas con descuento de stock  
✅ Trazabilidad completa  
✅ Autenticación JWT  
✅ 3 roles de usuario  
✅ Documentación Swagger

## 📊 Datos Pre-cargados

Al iniciar la aplicación se crean:

- 3 Roles: administrador, contador, aux_contable
- 2 Unidades: kg, unidad
- 3 Tipos: filete, cabeza, posta

## 🚀 Próximos Pasos

1. ✅ Crear usuario administrador
2. ✅ Crear productos (base y trabajados)
3. ✅ Registrar primera compra
4. ✅ Crear preparación
5. ✅ Registrar venta
6. ✅ Ver inventario

---

**¿Necesitas ayuda?** Consulta `MIGRACION.md` para guía detallada.
