package cl.pymeflow.plan.repository;

import cl.pymeflow.plan.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

    Optional<Plan> findByCodigoAndActivoTrue(String codigo);
}