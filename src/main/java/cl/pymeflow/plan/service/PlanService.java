package cl.pymeflow.plan.service;

import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.plan.dto.MiPlanResponse;
import cl.pymeflow.plan.exception.CapacidadNoDisponibleException;
import cl.pymeflow.plan.model.Capacidad;
import cl.pymeflow.plan.model.Plan;
import cl.pymeflow.plan.model.TipoLimite;
import cl.pymeflow.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public PlanService(
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public MiPlanResponse obtenerMiPlan() {

        Plan plan = obtenerPlanActual();

        return new MiPlanResponse(
                plan.getCodigo(),
                plan.getNombre(),
                plan.getDescripcion(),
                plan.getCapacidades(),
                plan.getLimites()
        );
    }

    @Transactional(readOnly = true)
    public boolean tieneCapacidad(
            Capacidad capacidad
    ) {
        return obtenerPlanActual()
                .tieneCapacidad(capacidad);
    }

    @Transactional(readOnly = true)
    public void exigirCapacidad(
            Capacidad capacidad
    ) {

        if (!tieneCapacidad(capacidad)) {
            throw new CapacidadNoDisponibleException(
                    capacidad
            );
        }
    }

    @Transactional(readOnly = true)
    public int obtenerLimite(
            TipoLimite tipoLimite
    ) {
        return obtenerPlanActual()
                .obtenerLimite(tipoLimite);
    }

    private Plan obtenerPlanActual() {

        Empresa empresa = usuarioAutenticadoService
                .obtenerUsuarioActual()
                .getEmpresa();

        Plan plan = empresa.getPlan();

        if (plan == null) {
            throw new IllegalStateException(
                    "La empresa no tiene un plan asignado"
            );
        }

        if (!plan.isActivo()) {
            throw new IllegalStateException(
                    "El plan de la empresa no está activo"
            );
        }

        return plan;
    }
}