package cl.pymeflow.venta.repository;

import cl.pymeflow.venta.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import cl.pymeflow.venta.model.EstadoVenta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface VentaRepository extends JpaRepository<Venta, UUID> {

    List<Venta> findAllByEmpresaIdOrderByFechaVentaDesc(
            UUID empresaId
    );

    Optional<Venta> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId
    );
    List<Venta> findTop5ByEmpresaIdAndEstadoOrderByFechaVentaDesc(
            UUID empresaId,
            EstadoVenta estado
    );
    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE v.empresa.id = :empresaId
          AND v.estado = :estado
        """)
    BigDecimal sumarTotalPorEmpresaYEstado(
            @Param("empresaId") UUID empresaId,
            @Param("estado") EstadoVenta estado
    );
}