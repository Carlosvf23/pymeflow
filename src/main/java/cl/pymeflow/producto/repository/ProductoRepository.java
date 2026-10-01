package cl.pymeflow.producto.repository;

import cl.pymeflow.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository
        extends JpaRepository<Producto, UUID> {

    List<Producto> findAllByEmpresaId(UUID empresaId);

    Optional<Producto> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId
    );
    long countByEmpresaId(UUID empresaId);

    boolean existsByEmpresaIdAndSku(
            UUID empresaId,
            String sku
    );

    boolean existsByEmpresaIdAndSkuAndIdNot(
            UUID empresaId,
            String sku,
            UUID id
    );
}