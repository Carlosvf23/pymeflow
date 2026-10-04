package cl.pymeflow.plan.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "planes")
public class Plan {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "plan_capacidades",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "capacidad", nullable = false, length = 60)
    private Set<Capacidad> capacidades = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "plan_limites",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "tipo_limite", length = 60)
    @Column(name = "valor", nullable = false)
    private Map<TipoLimite, Integer> limites = new HashMap<>();

    protected Plan() {
    }

    public UUID getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Set<Capacidad> getCapacidades() {
        return Set.copyOf(capacidades);
    }

    public Map<TipoLimite, Integer> getLimites() {
        return Map.copyOf(limites);
    }

    public boolean tieneCapacidad(Capacidad capacidad) {
        return capacidades.contains(capacidad);
    }

    public int obtenerLimite(TipoLimite tipoLimite) {
        return limites.getOrDefault(tipoLimite, 0);
    }
}