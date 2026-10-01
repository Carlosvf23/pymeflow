package cl.pymeflow.categoria.service;

import cl.pymeflow.categoria.dto.ActualizarCategoriaRequest;
import cl.pymeflow.categoria.dto.CategoriaResponse;
import cl.pymeflow.categoria.dto.CrearCategoriaRequest;
import cl.pymeflow.categoria.exception.CategoriaNoEncontradaException;
import cl.pymeflow.categoria.exception.NombreCategoriaDuplicadoException;
import cl.pymeflow.categoria.model.Categoria;
import cl.pymeflow.categoria.repository.CategoriaRepository;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public CategoriaResponse crear(CrearCategoriaRequest request) {

        Empresa empresa =
                usuarioAutenticadoService.obtenerUsuarioActual()
                        .getEmpresa();

        UUID empresaId = empresa.getId();

        if (categoriaRepository.existsByEmpresaIdAndNombre(
                empresaId,
                request.nombre()
        )) {
            throw new NombreCategoriaDuplicadoException(
                    request.nombre()
            );
        }

        Categoria categoria = new Categoria(
                empresa,
                request.nombre(),
                request.descripcion()
        );

        Categoria guardada =
                categoriaRepository.save(categoria);

        return convertirAResponse(guardada);
    }

    public List<CategoriaResponse> listar() {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return categoriaRepository
                .findAllByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public CategoriaResponse buscarPorId(UUID id) {
        return convertirAResponse(
                buscarEntidadPorId(id)
        );
    }

    @Transactional
    public CategoriaResponse actualizar(
            UUID id,
            ActualizarCategoriaRequest request
    ) {

        Categoria categoria =
                buscarEntidadPorId(id);

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        if (categoriaRepository
                .existsByEmpresaIdAndNombreAndIdNot(
                        empresaId,
                        request.nombre(),
                        id
                )) {
            throw new NombreCategoriaDuplicadoException(
                    request.nombre()
            );
        }

        categoria.actualizarDatos(
                request.nombre(),
                request.descripcion()
        );

        return convertirAResponse(categoria);
    }

    @Transactional
    public CategoriaResponse activar(UUID id) {

        Categoria categoria =
                buscarEntidadPorId(id);

        categoria.activar();

        return convertirAResponse(categoria);
    }

    @Transactional
    public CategoriaResponse desactivar(UUID id) {

        Categoria categoria =
                buscarEntidadPorId(id);

        categoria.desactivar();

        return convertirAResponse(categoria);
    }

    public Categoria buscarEntidadPorId(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return categoriaRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new CategoriaNoEncontradaException(id)
                );
    }

    private CategoriaResponse convertirAResponse(
            Categoria categoria
    ) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getEmpresa().getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado(),
                categoria.getFechaCreacion(),
                categoria.getFechaActualizacion()
        );
    }
}