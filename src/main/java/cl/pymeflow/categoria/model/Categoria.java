package cl.pymeflow.categoria.model;

import cl.pymeflow.empresa.model.Empresa;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCategoria estado;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    protected Categoria() {
    }

    public Categoria(
            Empresa empresa,
            String nombre,
            String descripcion
    ) {
        this.id = UUID.randomUUID();
        this.empresa = empresa;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = EstadoCategoria.ACTIVA;
    }

    @PrePersist
    public void prePersist() {
        Instant ahora = Instant.now();
        this.fechaCreacion = ahora;
        this.fechaActualizacion = ahora;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = Instant.now();
    }

    public void actualizarDatos(
            String nombre,
            String descripcion
    ) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public void activar() {
        this.estado = EstadoCategoria.ACTIVA;
    }

    public void desactivar() {
        this.estado = EstadoCategoria.INACTIVA;
    }

    public UUID getId() {
        return id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoCategoria getEstado() {
        return estado;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }
}