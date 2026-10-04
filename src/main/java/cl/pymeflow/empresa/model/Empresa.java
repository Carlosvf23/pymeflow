package cl.pymeflow.empresa.model;

import cl.pymeflow.plan.model.Plan;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "nombre_fantasia", length = 150)
    private String nombreFantasia;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String comuna;

    @Column(length = 100)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEmpresa estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    protected Empresa() {
    }

    public Empresa(
            String rut,
            String razonSocial,
            String nombreFantasia,
            String email,
            String telefono,
            String direccion,
            String comuna,
            String region
    ) {
        this.rut = rut;
        this.razonSocial = razonSocial;
        this.nombreFantasia = nombreFantasia;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.comuna = comuna;
        this.region = region;
        this.estado = EstadoEmpresa.ACTIVA;
    }

    @PrePersist
    protected void prePersist() {
        Instant ahora = Instant.now();
        this.fechaCreacion = ahora;
        this.fechaActualizacion = ahora;
    }

    @PreUpdate
    protected void preUpdate() {
        this.fechaActualizacion = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getRut() {
        return rut;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getComuna() {
        return comuna;
    }

    public String getRegion() {
        return region;
    }

    public EstadoEmpresa getEstado() {
        return estado;
    }

    public Plan getPlan() {
        return plan;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void actualizarDatos(
            String razonSocial,
            String nombreFantasia,
            String email,
            String telefono,
            String direccion,
            String comuna,
            String region
    ) {
        this.razonSocial = razonSocial;
        this.nombreFantasia = nombreFantasia;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.comuna = comuna;
        this.region = region;
    }

    public void asignarPlan(Plan plan) {
        this.plan = plan;
    }

    public void suspender() {
        this.estado = EstadoEmpresa.SUSPENDIDA;
    }

    public void activar() {
        this.estado = EstadoEmpresa.ACTIVA;
    }

    public void desactivar() {
        this.estado = EstadoEmpresa.INACTIVA;
    }
}