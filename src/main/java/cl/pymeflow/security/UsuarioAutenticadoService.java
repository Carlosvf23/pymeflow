package cl.pymeflow.security;

import cl.pymeflow.usuario.model.Usuario;
import cl.pymeflow.usuario.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoService(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtenerUsuarioActual() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Usuario autenticado no encontrado"
                        )
                );
    }

    public UUID obtenerEmpresaId() {
        return obtenerUsuarioActual()
                .getEmpresa()
                .getId();
    }
}