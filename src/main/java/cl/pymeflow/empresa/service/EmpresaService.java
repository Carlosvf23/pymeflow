package cl.pymeflow.empresa.service;

import cl.pymeflow.empresa.dto.ActualizarEmpresaRequest;
import cl.pymeflow.empresa.dto.CrearEmpresaRequest;
import cl.pymeflow.empresa.dto.EmpresaResponse;
import cl.pymeflow.empresa.exception.EmpresaNoEncontradaException;
import cl.pymeflow.empresa.exception.RutEmpresaDuplicadoException;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.empresa.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public EmpresaResponse crear(CrearEmpresaRequest request) {

        if (empresaRepository.existsByRut(request.rut())) {
            throw new RutEmpresaDuplicadoException(request.rut());
        }

        Empresa empresa = new Empresa(
                request.rut(),
                request.razonSocial(),
                request.nombreFantasia(),
                request.email(),
                request.telefono(),
                request.direccion(),
                request.comuna(),
                request.region()
        );

        Empresa guardada = empresaRepository.save(empresa);

        return convertirAResponse(guardada);
    }

    public List<EmpresaResponse> listar() {
        return empresaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public EmpresaResponse buscarPorId(UUID id) {
        Empresa empresa = buscarEntidadPorId(id);
        return convertirAResponse(empresa);
    }

    @Transactional
    public EmpresaResponse actualizar(
            UUID id,
            ActualizarEmpresaRequest request
    ) {

        Empresa empresa = buscarEntidadPorId(id);

        empresa.actualizarDatos(
                request.razonSocial(),
                request.nombreFantasia(),
                request.email(),
                request.telefono(),
                request.direccion(),
                request.comuna(),
                request.region()
        );

        return convertirAResponse(empresa);
    }

    @Transactional
    public EmpresaResponse suspender(UUID id) {
        Empresa empresa = buscarEntidadPorId(id);
        empresa.suspender();

        return convertirAResponse(empresa);
    }

    @Transactional
    public EmpresaResponse activar(UUID id) {
        Empresa empresa = buscarEntidadPorId(id);
        empresa.activar();

        return convertirAResponse(empresa);
    }

    private Empresa buscarEntidadPorId(UUID id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNoEncontradaException(id));
    }

    private EmpresaResponse convertirAResponse(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getRut(),
                empresa.getRazonSocial(),
                empresa.getNombreFantasia(),
                empresa.getEmail(),
                empresa.getTelefono(),
                empresa.getDireccion(),
                empresa.getComuna(),
                empresa.getRegion(),
                empresa.getEstado(),
                empresa.getFechaCreacion(),
                empresa.getFechaActualizacion()
        );
    }
}