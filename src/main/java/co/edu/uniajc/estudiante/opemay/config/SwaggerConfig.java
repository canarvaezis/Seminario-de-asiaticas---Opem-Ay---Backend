package co.edu.uniajc.estudiante.opemay.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Pescadería API",
        version = "1.0.0",
        description = """
            ## API de Gestión de Pescadería

            Sistema para gestionar compras, preparaciones, inventario y ventas de productos de pescadería.

            ### Características:
            - **Autenticación JWT** - Sistema seguro de tokens
            - **Gestión de Compras** - Registro de compras con código de cargue
            - **Preparaciones** - Procesamiento de pescado (filetes, postas, cabezas)
            - **Control de Stock** - Inventario automático de productos trabajados
            - **Ventas** - Registro con descuento automático de stock
            - **Roles** - Administrador, Contador, Aux. Contable

            ### Autenticación:
            1. Registrar usuario: `POST /api/auth/registro`
            2. Login: `POST /api/auth/login`
            3. Copiar el `token` recibido y usarlo como: `Bearer <token>`
            """,
        contact = @Contact(
            name = "UNIAJC - Seminario",
            email = "pescaderia@uniajc.edu.co"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Servidor Local"),
        @Server(url = "https://pescaderia-api.example.com", description = "Servidor Producción")
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Token JWT obtenido en POST /api/auth/login. Formato: Bearer <token>"
)
public class SwaggerConfig {
}