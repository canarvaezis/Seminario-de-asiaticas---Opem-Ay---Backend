# Sistema de Gestión de Pescadería

API REST para la gestión completa de una pescadería, incluyendo compras, preparaciones, ventas e inventario.

## 🎯 Características

- **Gestión de Compras**: Registro de compras de pescado con trazabilidad completa
- **Preparaciones**: Procesamiento de pescado base en productos derivados (filetes, cabezas, postas)
- **Stock Inteligente**: Control automático de stock trabajado y sin trabajar
- **Ventas**: Registro de ventas con descuento automático de inventario
- **Inventario**: Consultas en tiempo real de disponibilidad
- **Autenticación**: Sistema JWT para seguridad
- **Roles**: Control de acceso basado en roles (administrador, contador, aux_contable)

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.4**
- **PostgreSQL**
- **Spring Data JPA**
- **Spring Security + JWT**
- **Swagger/OpenAPI 3**
- **Lombok**
- **Gradle**

## 📋 Requisitos Previos

- Java 21 o superior
- PostgreSQL 12 o superior
- Gradle 8.x (o usar el wrapper incluido)

## ⚙️ Configuración

### 1. Base de Datos

Crear la base de datos PostgreSQL:

```sql
CREATE DATABASE pescaderia_db;
```

### 2. Configuración de Aplicación

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pescaderia_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
```

### 3. Ejecutar la Aplicación

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 Documentación API

Una vez iniciada la aplicación, acceder a:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔑 Endpoints Principales

### Autenticación

```
POST /api/auth/registro  - Registrar nuevo usuario
POST /api/auth/login     - Iniciar sesión
```

### Productos

```
GET    /api/productos           - Listar todos los productos
POST   /api/productos           - Crear producto
GET    /api/productos/base      - Listar productos base (pescados)
GET    /api/productos/trabajados - Listar productos trabajados
```

### Compras

```
GET    /api/compras             - Listar compras
POST   /api/compras             - Registrar nueva compra
GET    /api/compras/{id}        - Obtener compra por ID
GET    /api/compras/inventario  - Ver inventario general
```

### Preparaciones

```
GET    /api/preparaciones       - Listar preparaciones
POST   /api/preparaciones       - Registrar nueva preparación
GET    /api/preparaciones/{id}  - Obtener preparación por ID
```

### Ventas

```
GET    /api/ventas              - Listar ventas
POST   /api/ventas              - Registrar nueva venta
GET    /api/ventas/{id}         - Obtener venta por ID
```

### Stock

```
GET    /api/stock/disponible         - Stock trabajado disponible
GET    /api/stock/compra/{compraId}  - Stock por compra
```

## 🗂️ Modelo de Datos

### Entidades Principales

1. **Usuario**: Gestión de usuarios del sistema
2. **Rol**: Roles de usuario (administrador, contador, aux_contable)
3. **Producto**: Catálogo de productos (base y trabajados)
4. **Compra**: Registro de compras de pescado
5. **Preparacion**: Procesamiento de pescado
6. **Venta**: Registro de ventas
7. **StockTrabajado**: Control de inventario procesado

### Flujo de Trabajo

1. **Compra**: Se registra una compra de pescado base
2. **Preparación**: Se procesa el pescado en productos derivados
3. **Stock**: Se actualiza automáticamente el inventario
4. **Venta**: Se vende producto (base o trabajado) con descuento automático de stock

## 👥 Roles del Sistema

- **Administrador**: Acceso completo al sistema
- **Contador**: Gestión de compras y ventas
- **Aux_Contable**: Asistente contable

## 🔒 Seguridad

El sistema utiliza:

- JWT para autenticación
- Contraseñas encriptadas con BCrypt
- Control de acceso basado en roles

## 📊 Datos Iniciales

Al iniciar la aplicación, se cargan automáticamente:

- **Roles**: administrador, contador, aux_contable
- **Unidades de Medida**: kg, unidad
- **Tipos de Procedimiento**: filete, cabeza, posta

## 🧪 Testing

```bash
# Ejecutar tests
./gradlew test

# Ver reporte de coverage
./gradlew jacocoTestReport
```

## 📝 Ejemplo de Uso

### 1. Registrar un usuario

```json
POST /api/auth/registro
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan@pescaderia.com",
  "contrasena": "password123",
  "rolId": 1
}
```

### 2. Registrar una compra

```json
POST /api/compras
{
  "codCargue": "CARGUE-001",
  "usuarioId": 1,
  "productoBaseId": 1,
  "unidadMedidaId": 1,
  "cantidadTotal": 100.500,
  "valorUnitarioCompra": 15000.00
}
```

### 3. Crear una preparación

```json
POST /api/preparaciones
{
  "compraId": 1,
  "usuarioId": 1,
  "cantidadEntrada": 50.000,
  "detalles": [
    {
      "productoResultadoId": 2,
      "cantidadSalida": 30.000
    },
    {
      "productoResultadoId": 3,
      "cantidadSalida": 15.000
    }
  ]
}
```

### 4. Registrar una venta

```json
POST /api/ventas
{
  "usuarioId": 1,
  "detalles": [
    {
      "compraId": 1,
      "productoId": 2,
      "cantidad": 10.000,
      "precioUnitario": 25000.00
    }
  ]
}
```

## 📄 Licencia

Este proyecto es parte del curso de desarrollo de software de la Universidad Antonio José Camacho.

## 👨‍💻 Autor

Estudiante UNIAJC - Proyecto de Pescadería
