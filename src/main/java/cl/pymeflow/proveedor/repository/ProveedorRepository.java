package cl.pymeflow.proveedor.repository;

import cl.pymeflow.proveedor.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProveedorRepository
        extends JpaRepository<Proveedor, UUID> {

    List<Proveedor> findAllByEmpresaId(UUID empresaId);

    Optional<Proveedor> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId
    );

    boolean existsByEmpresaIdAndRut(
            UUID empresaId,
            String rut
    );

    boolean existsByEmpresaIdAndRutAndIdNot(
            UUID empresaId,
            String rut,
            UUID id
    );
}