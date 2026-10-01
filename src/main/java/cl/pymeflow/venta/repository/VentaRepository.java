package cl.pymeflow.venta.repository;

import cl.pymeflow.venta.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VentaRepository extends JpaRepository<Venta, UUID> {

    List<Venta> findAllByEmpresaIdOrderByFechaVentaDesc(
            UUID empresaId
    );

    Optional<Venta> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId
    );
}