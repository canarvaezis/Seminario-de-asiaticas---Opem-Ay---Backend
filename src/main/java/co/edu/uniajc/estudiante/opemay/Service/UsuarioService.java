package co.edu.uniajc.estudiante.opemay.Service;

import co.edu.uniajc.estudiante.opemay.IRespository.RolRepository;
import co.edu.uniajc.estudiante.opemay.IRespository.UsuarioRepository;
import co.edu.uniajc.estudiante.opemay.dto.LoginRequestDTO;
import co.edu.uniajc.estudiante.opemay.dto.LoginResponseDTO;
import co.edu.uniajc.estudiante.opemay.dto.UsuarioRegistroDTO;
import co.edu.uniajc.estudiante.opemay.model.Ciudad;
import co.edu.uniajc.estudiante.opemay.model.Rol;
import co.edu.uniajc.estudiante.opemay.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CiudadService ciudadService;
    
    @Transactional
    public Usuario registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        
        Rol rol;
        if (dto.getRolId() != null) {
            rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        } else {
            // Por defecto, cualquier registro sin rol especificado es CLIENTE
            rol = rolRepository.findByNombre("cliente")
                    .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no configurado en la base de datos"));
        }
        
        Ciudad ciudad = ciudadService.obtenerEntidad(dto.getCiudadId());

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setCiudad(ciudad.getNombre());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(rol);
        
        return usuarioRepository.save(usuario);
    }
    
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        
        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasena())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        
        String token = jwtService.generateToken(usuario.getCorreo());
        
        return new LoginResponseDTO(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
            usuario.getCiudad(),
                usuario.getRol().getNombre()
        );
    }
    
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
