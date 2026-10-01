package cl.pymeflow.inventario.repository;

import cl.pymeflow.inventario.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface MovimientoInventarioRepository
        extends JpaRepository<MovimientoInventario, UUID> {

    List<MovimientoInventario> findAllByEmpresaIdAndProductoIdOrderByFechaMovimientoDesc(
            UUID empresaId,
            UUID productoId
    );

    @Query("""
            SELECT COALESCE(SUM(
                CASE
                    WHEN m.tipo IN (
                        cl.pymeflow.inventario.model.TipoMovimientoInventario.ENTRADA_INICIAL,
                        cl.pymeflow.inventario.model.TipoMovimientoInventario.ENTRADA_COMPRA,
                        cl.pymeflow.inventario.model.TipoMovimientoInventario.AJUSTE_POSITIVO
                    )
                    THEN m.cantidad
                    ELSE -m.cantidad
                END
            ), 0)
            FROM MovimientoInventario m
            WHERE m.empresa.id = :empresaId
              AND m.producto.id = :productoId
            """)
    BigDecimal calcularStock(
            @Param("empresaId") UUID empresaId,
            @Param("productoId") UUID productoId
    );
}