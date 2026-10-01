package cl.pymeflow.compra.repository;

import cl.pymeflow.compra.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import cl.pymeflow.compra.model.EstadoCompra;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface CompraRepository extends JpaRepository<Compra, UUID> {

    List<Compra> findAllByEmpresaIdOrderByFechaCompraDesc(
            UUID empresaId
    );

    Optional<Compra> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId
    );
    List<Compra> findTop5ByEmpresaIdAndEstadoOrderByFechaCompraDesc(
            UUID empresaId,
            EstadoCompra estado
    );
    @Query("""
        SELECT COALESCE(SUM(c.total), 0)
        FROM Compra c
        WHERE c.empresa.id = :empresaId
          AND c.estado = :estado
        """)
    BigDecimal sumarTotalPorEmpresaYEstado(
            @Param("empresaId") UUID empresaId,
            @Param("estado") EstadoCompra estado
    );
}