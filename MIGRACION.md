# Guía de Migración: De E-commerce a Sistema de Pescadería

## 📋 Resumen de Cambios

Se ha transformado completamente la API de un sistema de e-commerce a un sistema de gestión de pescadería con las siguientes características:

### Nuevas Entidades

- ✅ **Rol**: Gestión de roles de usuario
- ✅ **Usuario**: Sistema de usuarios con autenticación
- ✅ **UnidadMedida**: Unidades de medida (kg, unidad)
- ✅ **Producto**: Productos base y trabajados
- ✅ **TipoProcedimiento**: Tipos de procesamiento (filete, cabeza, posta)
- ✅ **Compra**: Registro de compras de pescado
- ✅ **Preparacion**: Procesamiento de pescado
- ✅ **DetallePreparacion**: Detalles de cada preparación
- ✅ **StockTrabajado**: Control de inventario procesado
- ✅ **Venta**: Registro de ventas
- ✅ **DetalleVenta**: Detalles de cada venta

### Funcionalidades

- ✅ Trazabilidad completa de compras
- ✅ Control automático de stock
- ✅ Procesamiento de pescado en productos derivados
- ✅ Ventas con descuento automático de inventario
- ✅ Autenticación JWT
- ✅ Control de acceso por roles
- ✅ Documentación con Swagger

## 🔧 Pasos para la Migración

### 1. Instalar PostgreSQL

Si no tienes PostgreSQL instalado:

**Windows:**

1. Descargar desde https://www.postgresql.org/download/windows/
2. Instalar con configuración por defecto
3. Recordar la contraseña del usuario `postgres`

**Linux (Ubuntu/Debian):**

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**macOS:**

```bash
brew install postgresql@14
brew services start postgresql@14
```

### 2. Crear la Base de Datos

Abrir terminal PostgreSQL (psql):

```sql
CREATE DATABASE pescaderia_db;
```

O usar el script completo incluido:

```bash
psql -U postgres -f src/main/resources/db/schema.sql
```

### 3. Configurar la Aplicación

Editar `src/main/resources/application.properties`:

```properties
# Cambiar estos valores según tu configuración:
spring.datasource.url=jdbc:postgresql://localhost:5432/pescaderia_db
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA_AQUI
```

### 4. Recargar Dependencias

El proyecto ahora usa PostgreSQL y JPA. Recargar dependencias:

**En la terminal:**

```bash
# Windows
gradlew.bat clean build --refresh-dependencies

# Linux/Mac
./gradlew clean build --refresh-dependencies
```

**En VS Code:**

1. Ctrl+Shift+P
2. Buscar "Java: Clean Java Language Server Workspace"
3. Reload Window

**En IntelliJ IDEA:**

1. Botón derecho en `build.gradle`
2. Gradle → Reload Gradle Project

### 5. Verificar la Compilación

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

### 6. Ejecutar la Aplicación

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

La aplicación iniciará en: `http://localhost:8080`

### 7. Verificar Datos Iniciales

Al iniciar, la aplicación cargará automáticamente:

- 3 roles (administrador, contador, aux_contable)
- 2 unidades de medida (kg, unidad)
- 3 tipos de procedimiento (filete, cabeza, posta)

## 📊 Estructura de la Base de Datos

### Diagrama Conceptual

```
Usuario → Compra → Preparacion → DetallePreparacion → Producto
                ↓                                          ↑
            StockTrabajado ←───────────────────────────────┘
                ↓
             Venta → DetalleVenta
```

### Flujo de Trabajo

1. **Registrar Usuario** (con rol asignado)
2. **Crear Producto Base** (ej: Salmón entero)
3. **Crear Productos Trabajados** (ej: Filete de salmón)
4. **Registrar Compra** de producto base
5. **Crear Preparación** (procesar pescado)
   - Ingresa cantidad de producto base
   - Sale productos derivados (filetes, cabezas, etc.)
   - Se actualiza automáticamente el stock trabajado
6. **Registrar Venta** de productos (base o trabajados)
   - Descuenta automáticamente del inventario

## 🔑 Primeros Pasos Después de la Migración

### 1. Crear un Usuario Administrador

```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Admin",
    "apellido": "Sistema",
    "correo": "admin@pescaderia.com",
    "contrasena": "admin123",
    "rolId": 1
  }'
```

### 2. Iniciar Sesión

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "admin@pescaderia.com",
    "contrasena": "admin123"
  }'
```

Guardar el `token` de la respuesta.

### 3. Crear Productos

**Producto Base (Pescado Entero):**

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{
    "nombre": "Salmón Entero",
    "unidadMedidaId": 1,
    "esBase": true
  }'
```

**Producto Trabajado (Filete):**

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{
    "nombre": "Filete de Salmón",
    "unidadMedidaId": 1,
    "esBase": false
  }'
```

### 4. Ver Documentación Completa

Abrir en navegador: `http://localhost:8080/swagger-ui.html`

## 🗑️ Archivos Antiguos a Eliminar (Opcional)

Si deseas limpiar el proyecto, puedes eliminar estos archivos del sistema anterior:

```
src/main/java/co/edu/uniajc/estudiante/opemay/model/
  - Cart.java
  - CartItem.java
  - Category.java
  - Order.java
  - OrderItem.java
  - OrderStatus.java
  - PaymentStatus.java
  - User.java (reemplazado por Usuario.java)

src/main/java/co/edu/uniajc/estudiante/opemay/IRespository/
  - CartRepository.java
  - CategoryRepository.java
  - OrderRepository.java
  - UserRepository.java (reemplazado por UsuarioRepository.java)

src/main/java/co/edu/uniajc/estudiante/opemay/Service/
  - CartService.java
  - CategoryService.java
  - OrderService.java
  - UserService.java (mantener JwtService.java)

src/main/java/co/edu/uniajc/estudiante/opemay/restController/
  - CartController.java
  - CategoryController.java
  - HomeController.java
  - OrderController.java
  - UserController.java

src/main/java/co/edu/uniajc/estudiante/opemay/dto/
  - AddToCartRequest.java
  - CartResponse.java
  - CategoryCreateDTO.java
  - CategoryUpdateDTO.java
  - CreateCategoryRequest.java
  - LoginRequest.java (reemplazado por LoginRequestDTO.java)
  - OrderRequest.java
  - ProductDTO.java
  - etc.
```

## 🧪 Probar la API

### Ver Inventario

```bash
curl http://localhost:8080/api/compras/inventario \
  -H "Authorization: Bearer TU_TOKEN"
```

### Ver Stock Trabajado Disponible

```bash
curl http://localhost:8080/api/stock/disponible \
  -H "Authorization: Bearer TU_TOKEN"
```

### Listar Todas las Compras

```bash
curl http://localhost:8080/api/compras \
  -H "Authorization: Bearer TU_TOKEN"
```

## 📝 Notas Importantes

1. **Contraseñas**: Se encriptan automáticamente con BCrypt
2. **Tokens JWT**: Expiran en 24 horas (configurable en `application.properties`)
3. **Stock**: Se maneja automáticamente mediante triggers de base de datos (simulados con lógica de servicio)
4. **Validaciones**: Todas las operaciones incluyen validaciones de negocio
5. **Transacciones**: Las operaciones complejas usan `@Transactional`

## 🐛 Solución de Problemas

### Error: "Cannot connect to PostgreSQL"

- Verificar que PostgreSQL esté corriendo
- Verificar usuario y contraseña en `application.properties`
- Verificar que la base de datos `pescaderia_db` existe

### Error: "Table 'x' doesn't exist"

- Verificar que `spring.jpa.hibernate.ddl-auto=update` en properties
- La primera vez, JPA creará las tablas automáticamente

### Error de compilación JPA

- Ejecutar `./gradlew clean build --refresh-dependencies`
- Recargar proyecto en el IDE

### Puerto 8080 en uso

- Cambiar puerto en `application.properties`:
  ```properties
  server.port=8081
  ```

## 📚 Recursos Adicionales

- **README**: Ver `README_PESCADERIA.md` para documentación completa
- **Schema SQL**: Ver `src/main/resources/db/schema.sql`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console** (testing): Comentado en producción

## ✅ Checklist de Migración

- [ ] PostgreSQL instalado y corriendo
- [ ] Base de datos `pescaderia_db` creada
- [ ] `application.properties` configurado
- [ ] Dependencias de Gradle recargadas
- [ ] Proyecto compila sin errores
- [ ] Aplicación inicia correctamente
- [ ] Datos iniciales cargados
- [ ] Usuario administrador creado
- [ ] Primera compra registrada
- [ ] Sistema probado con Swagger

---

**¡Migración completada! 🎉**

El sistema ahora está listo para gestionar una pescadería con trazabilidad completa.
