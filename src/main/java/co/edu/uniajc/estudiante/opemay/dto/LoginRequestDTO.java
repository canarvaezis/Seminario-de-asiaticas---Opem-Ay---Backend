package co.edu.uniajc.estudiante.opemay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String correo;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}
