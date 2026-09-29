package cl.pymeflow.auth.service;

import cl.pymeflow.auth.dto.LoginRequest;
import cl.pymeflow.auth.dto.LoginResponse;
import cl.pymeflow.auth.exception.CredencialesInvalidasException;
import cl.pymeflow.usuario.model.EstadoUsuario;
import cl.pymeflow.usuario.model.Usuario;
import cl.pymeflow.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(CredencialesInvalidasException::new);

        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new CredencialesInvalidasException();
        }

        if (!passwordEncoder.matches(
                request.password(),
                usuario.getPassword()
        )) {
            throw new CredencialesInvalidasException();
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getEmpresa().getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol()
        );
    }
}