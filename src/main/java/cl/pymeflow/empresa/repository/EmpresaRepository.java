package cl.pymeflow.empresa.repository;

import cl.pymeflow.empresa.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    boolean existsByRut(String rut);

}