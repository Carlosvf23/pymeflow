package cl.pymeflow.usuario.repository;

import cl.pymeflow.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAllByEmpresaId(UUID empresaId);

    Optional<Usuario> findByIdAndEmpresaId(UUID id, UUID empresaId);
}