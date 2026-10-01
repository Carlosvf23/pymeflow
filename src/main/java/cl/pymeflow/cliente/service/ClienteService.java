package cl.pymeflow.cliente.service;

import cl.pymeflow.cliente.dto.ActualizarClienteRequest;
import cl.pymeflow.cliente.dto.ClienteResponse;
import cl.pymeflow.cliente.dto.CrearClienteRequest;
import cl.pymeflow.cliente.exception.ClienteNoEncontradoException;
import cl.pymeflow.cliente.exception.RutClienteDuplicadoException;
import cl.pymeflow.cliente.model.Cliente;
import cl.pymeflow.cliente.repository.ClienteRepository;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ClienteService(
            ClienteRepository clienteRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public ClienteResponse crear(CrearClienteRequest request) {

        Empresa empresa =
                usuarioAutenticadoService.obtenerUsuarioActual()
                        .getEmpresa();

        UUID empresaId = empresa.getId();

        if (request.rut() != null
                && clienteRepository.existsByEmpresaIdAndRut(
                empresaId,
                request.rut()
        )) {
            throw new RutClienteDuplicadoException(request.rut());
        }

        Cliente cliente = new Cliente(
                empresa,
                request.rut(),
                request.razonSocial(),
                request.email(),
                request.telefono(),
                request.direccion(),
                request.comuna(),
                request.region()
        );

        Cliente guardado = clienteRepository.save(cliente);

        return convertirAResponse(guardado);
    }

    public List<ClienteResponse> listar() {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return clienteRepository.findAllByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ClienteResponse buscarPorId(UUID id) {
        return convertirAResponse(buscarEntidadPorId(id));
    }

    @Transactional
    public ClienteResponse actualizar(
            UUID id,
            ActualizarClienteRequest request
    ) {

        Cliente cliente = buscarEntidadPorId(id);

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        if (request.rut() != null
                && clienteRepository.existsByEmpresaIdAndRutAndIdNot(
                empresaId,
                request.rut(),
                id
        )) {
            throw new RutClienteDuplicadoException(request.rut());
        }

        cliente.actualizarDatos(
                request.rut(),
                request.razonSocial(),
                request.email(),
                request.telefono(),
                request.direccion(),
                request.comuna(),
                request.region()
        );

        return convertirAResponse(cliente);
    }

    @Transactional
    public ClienteResponse activar(UUID id) {

        Cliente cliente = buscarEntidadPorId(id);
        cliente.activar();

        return convertirAResponse(cliente);
    }

    @Transactional
    public ClienteResponse desactivar(UUID id) {

        Cliente cliente = buscarEntidadPorId(id);
        cliente.desactivar();

        return convertirAResponse(cliente);
    }

    private Cliente buscarEntidadPorId(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return clienteRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new ClienteNoEncontradoException(id)
                );
    }

    private ClienteResponse convertirAResponse(Cliente cliente) {

        return new ClienteResponse(
                cliente.getId(),
                cliente.getEmpresa().getId(),
                cliente.getRut(),
                cliente.getRazonSocial(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getComuna(),
                cliente.getRegion(),
                cliente.getEstado(),
                cliente.getFechaCreacion(),
                cliente.getFechaActualizacion()
        );
    }
}