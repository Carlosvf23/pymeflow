package cl.pymeflow.categoria.repository;

import cl.pymeflow.categoria.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository
        extends JpaRepository<Categoria, UUID> {

    List<Categoria> findAllByEmpresaId(UUID empresaId);

    Optional<Categoria> findByIdAndEmpresaId(
            UUID id,
            UUID empresaId
    );

    boolean existsByEmpresaIdAndNombre(
            UUID empresaId,
            String nombre
    );

    boolean existsByEmpresaIdAndNombreAndIdNot(
            UUID empresaId,
            String nombre,
            UUID id
    );
}