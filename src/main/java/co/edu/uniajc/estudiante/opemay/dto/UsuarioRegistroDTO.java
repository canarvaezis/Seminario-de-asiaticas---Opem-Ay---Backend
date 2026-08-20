package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String apellido;
    
    @Email(message = "Email inválido")
    private String correo;

    private Long ciudadId;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    /**
     * Opcional. Si no se envía, se asigna CLIENTE por defecto.
     * Solo ADMINISTRADOR debería enviar un rolId distinto.
     */
    private Long rolId;
}
