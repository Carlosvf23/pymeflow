package cl.pymeflow.compra.model;

import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.proveedor.model.Proveedor;
import jakarta.persistence.*;
import cl.pymeflow.compra.exception.CompraEstadoInvalidoException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "compras")
public class Compra {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(name = "numero_documento", length = 50)
    private String numeroDocumento;

    @Column(name = "fecha_compra", nullable = false)
    private Instant fechaCompra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCompra estado;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(length = 500)
    private String observacion;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @OneToMany(
            mappedBy = "compra",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DetalleCompra> detalles = new ArrayList<>();

    protected Compra() {
    }

    public Compra(
            Empresa empresa,
            Proveedor proveedor,
            String numeroDocumento,
            Instant fechaCompra,
            String observacion
    ) {
        this.id = UUID.randomUUID();
        this.empresa = empresa;
        this.proveedor = proveedor;
        this.numeroDocumento = numeroDocumento;
        this.fechaCompra = fechaCompra;
        this.observacion = observacion;
        this.estado = EstadoCompra.BORRADOR;
        this.total = BigDecimal.ZERO;
    }

    public void agregarDetalle(DetalleCompra detalle) {

        detalle.asignarCompra(this);
        detalles.add(detalle);

        recalcularTotal();
    }

    private void recalcularTotal() {
        this.total = detalles.stream()
                .map(DetalleCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirmar() {

        if (this.estado != EstadoCompra.BORRADOR) {
            throw new CompraEstadoInvalidoException(
                    "Solo se puede confirmar una compra en estado BORRADOR"
            );
        }

        this.estado = EstadoCompra.CONFIRMADA;
    }

    public void anular() {
        this.estado = EstadoCompra.ANULADA;
    }

    @PrePersist
    protected void prePersist() {
        Instant ahora = Instant.now();

        fechaCreacion = ahora;
        fechaActualizacion = ahora;
    }

    @PreUpdate
    protected void preUpdate() {
        fechaActualizacion = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public Instant getFechaCompra() {
        return fechaCompra;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getObservacion() {
        return observacion;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }
}