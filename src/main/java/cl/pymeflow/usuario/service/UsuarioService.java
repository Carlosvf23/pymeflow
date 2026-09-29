package cl.pymeflow.usuario.service;

import cl.pymeflow.empresa.exception.EmpresaNoEncontradaException;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.empresa.repository.EmpresaRepository;
import cl.pymeflow.usuario.dto.ActualizarUsuarioRequest;
import cl.pymeflow.usuario.dto.CrearUsuarioRequest;
import cl.pymeflow.usuario.dto.UsuarioResponse;
import cl.pymeflow.usuario.exception.EmailUsuarioDuplicadoException;
import cl.pymeflow.usuario.exception.UsuarioNoEncontradoException;
import cl.pymeflow.usuario.model.Usuario;
import cl.pymeflow.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailUsuarioDuplicadoException(request.email());
        }

        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() ->
                        new EmpresaNoEncontradaException(request.empresaId())
                );

        Usuario usuario = new Usuario(
                empresa,
                request.nombre(),
                request.apellido(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.rol()
        );

        Usuario guardado = usuarioRepository.save(usuario);

        return convertirAResponse(guardado);
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(UUID id) {
        return convertirAResponse(buscarEntidadPorId(id));
    }

    @Transactional
    public UsuarioResponse actualizar(
            UUID id,
            ActualizarUsuarioRequest request
    ) {
        Usuario usuario = buscarEntidadPorId(id);

        if (!usuario.getEmail().equals(request.email())
                && usuarioRepository.existsByEmail(request.email())) {
            throw new EmailUsuarioDuplicadoException(request.email());
        }

        usuario.actualizarDatos(
                request.nombre(),
                request.apellido(),
                request.email(),
                request.rol()
        );

        return convertirAResponse(usuario);
    }

    @Transactional
    public UsuarioResponse bloquear(UUID id) {
        Usuario usuario = buscarEntidadPorId(id);
        usuario.bloquear();

        return convertirAResponse(usuario);
    }

    @Transactional
    public UsuarioResponse activar(UUID id) {
        Usuario usuario = buscarEntidadPorId(id);
        usuario.activar();

        return convertirAResponse(usuario);
    }

    @Transactional
    public UsuarioResponse desactivar(UUID id) {
        Usuario usuario = buscarEntidadPorId(id);
        usuario.desactivar();

        return convertirAResponse(usuario);
    }

    private Usuario buscarEntidadPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    private UsuarioResponse convertirAResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmpresa().getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getEstado(),
                usuario.getFechaCreacion(),
                usuario.getFechaActualizacion()
        );
    }
}