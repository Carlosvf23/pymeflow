package cl.pymeflow.compra.repository;

import cl.pymeflow.compra.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompraRepository extends JpaRepository<Compra, UUID> {

    List<Compra> findAllByEmpresaIdOrderByFechaCompraDesc(
            UUID empresaId
    );

    Optional<Compra> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId
    );
}