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
import cl.pymeflow.security.UsuarioAutenticadoService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailUsuarioDuplicadoException(request.email());
        }

        Empresa empresa =
                usuarioAutenticadoService.obtenerUsuarioActual()
                        .getEmpresa();

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

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return usuarioRepository.findAllByEmpresaId(empresaId)
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

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return usuarioRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new UsuarioNoEncontradoException(id)
                );
    }
    private Usuario buscarUsuarioDeEmpresaActual(UUID usuarioId) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return usuarioRepository
                .findByIdAndEmpresaId(usuarioId, empresaId)
                .orElseThrow(() ->
                        new UsuarioNoEncontradoException(usuarioId)
                );
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