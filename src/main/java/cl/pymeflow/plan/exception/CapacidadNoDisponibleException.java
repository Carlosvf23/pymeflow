package cl.pymeflow.plan.exception;

import cl.pymeflow.plan.model.Capacidad;

public class CapacidadNoDisponibleException extends RuntimeException {

    public CapacidadNoDisponibleException(Capacidad capacidad) {
        super(
                "La capacidad " + capacidad +
                        " no está disponible en el plan actual"
        );
    }
}