package co.edu.uniajc.estudiante.opemay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private Long usuarioId;
    private String nombre;
    private String correo;
    private String ciudad;
    private String rol;
    
    public LoginResponseDTO(String token, Long usuarioId, String nombre, String correo, String ciudad, String rol) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.correo = correo;
        this.ciudad = ciudad;
        this.rol = rol;
    }
}
