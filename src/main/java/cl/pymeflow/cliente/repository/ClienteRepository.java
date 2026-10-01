package cl.pymeflow.cliente.repository;

import cl.pymeflow.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    List<Cliente> findAllByEmpresaId(UUID empresaId);

    Optional<Cliente> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId

    );
    long countByEmpresaId(UUID empresaId);

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