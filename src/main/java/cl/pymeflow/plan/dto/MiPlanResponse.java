package cl.pymeflow.plan.dto;

import cl.pymeflow.plan.model.Capacidad;
import cl.pymeflow.plan.model.TipoLimite;

import java.util.Map;
import java.util.Set;

public record MiPlanResponse(
        String codigo,
        String nombre,
        String descripcion,
        Set<Capacidad> capacidades,
        Map<TipoLimite, Integer> limites
) {
}