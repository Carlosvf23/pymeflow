package cl.pymeflow.proveedor.model;

import cl.pymeflow.empresa.model.Empresa;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(length = 12)
    private String rut;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(length = 150)
    private String contacto;

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

    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProveedor estado;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    protected Proveedor() {
    }

    public Proveedor(
            Empresa empresa,
            String rut,
            String razonSocial,
            String contacto,
            String email,
            String telefono,
            String direccion,
            String comuna,
            String region,
            String sitioWeb
    ) {
        this.id = UUID.randomUUID();
        this.empresa = empresa;
        this.rut = rut;
        this.razonSocial = razonSocial;
        this.contacto = contacto;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.comuna = comuna;
        this.region = region;
        this.sitioWeb = sitioWeb;
        this.estado = EstadoProveedor.ACTIVO;
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
            String rut,
            String razonSocial,
            String contacto,
            String email,
            String telefono,
            String direccion,
            String comuna,
            String region,
            String sitioWeb
    ) {
        this.rut = rut;
        this.razonSocial = razonSocial;
        this.contacto = contacto;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.comuna = comuna;
        this.region = region;
        this.sitioWeb = sitioWeb;
    }

    public void activar() {
        this.estado = EstadoProveedor.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoProveedor.INACTIVO;
    }

    public UUID getId() {
        return id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public String getRut() {
        return rut;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getContacto() {
        return contacto;
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

    public String getSitioWeb() {
        return sitioWeb;
    }

    public EstadoProveedor getEstado() {
        return estado;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }
}