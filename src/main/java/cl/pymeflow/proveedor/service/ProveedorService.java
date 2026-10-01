package cl.pymeflow.proveedor.service;

import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.proveedor.dto.ActualizarProveedorRequest;
import cl.pymeflow.proveedor.dto.CrearProveedorRequest;
import cl.pymeflow.proveedor.dto.ProveedorResponse;
import cl.pymeflow.proveedor.exception.ProveedorNoEncontradoException;
import cl.pymeflow.proveedor.exception.RutProveedorDuplicadoException;
import cl.pymeflow.proveedor.model.Proveedor;
import cl.pymeflow.proveedor.repository.ProveedorRepository;
import cl.pymeflow.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ProveedorService(
            ProveedorRepository proveedorRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.proveedorRepository = proveedorRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public ProveedorResponse crear(CrearProveedorRequest request) {

        Empresa empresa =
                usuarioAutenticadoService.obtenerUsuarioActual()
                        .getEmpresa();

        UUID empresaId = empresa.getId();

        if (request.rut() != null
                && proveedorRepository.existsByEmpresaIdAndRut(
                empresaId,
                request.rut()
        )) {
            throw new RutProveedorDuplicadoException(request.rut());
        }

        Proveedor proveedor = new Proveedor(
                empresa,
                request.rut(),
                request.razonSocial(),
                request.contacto(),
                request.email(),
                request.telefono(),
                request.direccion(),
                request.comuna(),
                request.region(),
                request.sitioWeb()
        );

        Proveedor guardado =
                proveedorRepository.save(proveedor);

        return convertirAResponse(guardado);
    }

    public List<ProveedorResponse> listar() {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return proveedorRepository
                .findAllByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ProveedorResponse buscarPorId(UUID id) {
        return convertirAResponse(
                buscarEntidadPorId(id)
        );
    }

    @Transactional
    public ProveedorResponse actualizar(
            UUID id,
            ActualizarProveedorRequest request
    ) {

        Proveedor proveedor =
                buscarEntidadPorId(id);

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        if (request.rut() != null
                && proveedorRepository
                .existsByEmpresaIdAndRutAndIdNot(
                        empresaId,
                        request.rut(),
                        id
                )) {
            throw new RutProveedorDuplicadoException(
                    request.rut()
            );
        }

        proveedor.actualizarDatos(
                request.rut(),
                request.razonSocial(),
                request.contacto(),
                request.email(),
                request.telefono(),
                request.direccion(),
                request.comuna(),
                request.region(),
                request.sitioWeb()
        );

        return convertirAResponse(proveedor);
    }

    @Transactional
    public ProveedorResponse activar(UUID id) {

        Proveedor proveedor =
                buscarEntidadPorId(id);

        proveedor.activar();

        return convertirAResponse(proveedor);
    }

    @Transactional
    public ProveedorResponse desactivar(UUID id) {

        Proveedor proveedor =
                buscarEntidadPorId(id);

        proveedor.desactivar();

        return convertirAResponse(proveedor);
    }

    private Proveedor buscarEntidadPorId(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return proveedorRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new ProveedorNoEncontradoException(id)
                );
    }

    private ProveedorResponse convertirAResponse(
            Proveedor proveedor
    ) {

        return new ProveedorResponse(
                proveedor.getId(),
                proveedor.getEmpresa().getId(),
                proveedor.getRut(),
                proveedor.getRazonSocial(),
                proveedor.getContacto(),
                proveedor.getEmail(),
                proveedor.getTelefono(),
                proveedor.getDireccion(),
                proveedor.getComuna(),
                proveedor.getRegion(),
                proveedor.getSitioWeb(),
                proveedor.getEstado(),
                proveedor.getFechaCreacion(),
                proveedor.getFechaActualizacion()
        );
    }
}