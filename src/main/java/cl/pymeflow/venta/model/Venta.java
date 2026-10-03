package cl.pymeflow.venta.model;

import cl.pymeflow.cliente.model.Cliente;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.venta.exception.VentaEstadoInvalidoException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "numero_documento", length = 50)
    private String numeroDocumento;

    @Column(name = "fecha_venta", nullable = false)
    private Instant fechaVenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVenta estado;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(length = 500)
    private String observacion;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @OneToMany(
            mappedBy = "venta",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DetalleVenta> detalles = new ArrayList<>();

    protected Venta() {
    }

    public Venta(
            Empresa empresa,
            Cliente cliente,
            String numeroDocumento,
            Instant fechaVenta,
            String observacion
    ) {
        this.id = UUID.randomUUID();
        this.empresa = empresa;
        this.cliente = cliente;
        this.numeroDocumento = numeroDocumento;
        this.fechaVenta = fechaVenta;
        this.observacion = observacion;
        this.estado = EstadoVenta.BORRADOR;
        this.total = BigDecimal.ZERO;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalle.asignarVenta(this);
        detalles.add(detalle);
        recalcularTotal();
    }

    public void actualizar(
            Cliente cliente,
            String numeroDocumento,
            Instant fechaVenta,
            String observacion
    ) {
        validarBorrador(
                "Solo se puede editar una venta en estado BORRADOR"
        );

        this.cliente = cliente;
        this.numeroDocumento = numeroDocumento;
        this.fechaVenta = fechaVenta;
        this.observacion = observacion;
    }

    public void reemplazarDetalles(List<DetalleVenta> nuevosDetalles) {
        validarBorrador(
                "Solo se pueden modificar los productos de una venta en estado BORRADOR"
        );

        this.detalles.clear();

        for (DetalleVenta detalle : nuevosDetalles) {
            detalle.asignarVenta(this);
            this.detalles.add(detalle);
        }

        recalcularTotal();
    }

    private void recalcularTotal() {
        this.total = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirmar() {
        validarBorrador(
                "Solo se puede confirmar una venta en estado BORRADOR"
        );

        this.estado = EstadoVenta.CONFIRMADA;
    }

    public void validarEliminacion() {
        validarBorrador(
                "Solo se puede eliminar una venta en estado BORRADOR"
        );
    }

    private void validarBorrador(String mensaje) {
        if (this.estado != EstadoVenta.BORRADOR) {
            throw new VentaEstadoInvalidoException(mensaje);
        }
    }

    public void anular() {
        this.estado = EstadoVenta.ANULADA;
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

    public Cliente getCliente() {
        return cliente;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public Instant getFechaVenta() {
        return fechaVenta;
    }

    public EstadoVenta getEstado() {
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

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
}