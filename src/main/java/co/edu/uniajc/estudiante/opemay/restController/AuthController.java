package co.edu.uniajc.estudiante.opemay.restController;

import co.edu.uniajc.estudiante.opemay.Service.UsuarioService;
import co.edu.uniajc.estudiante.opemay.dto.LoginRequestDTO;
import co.edu.uniajc.estudiante.opemay.dto.LoginResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.UsuarioRegistroDTO;
import co.edu.uniajc.estudiante.opemay.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro")
public class AuthController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        Usuario usuario = usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = usuarioService.login(dto);
        return ResponseEntity.ok(response);
    }
}
