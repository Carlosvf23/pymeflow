package cl.pymeflow.inventario.model;

import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.producto.model.Producto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimientoInventario tipo;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal cantidad;

    @Column(length = 500)
    private String observacion;

    @Column(name = "fecha_movimiento", nullable = false)
    private Instant fechaMovimiento;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    protected MovimientoInventario() {
    }

    public MovimientoInventario(
            Empresa empresa,
            Producto producto,
            TipoMovimientoInventario tipo,
            BigDecimal cantidad,
            String observacion
    ) {
        this.id = UUID.randomUUID();
        this.empresa = empresa;
        this.producto = producto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.observacion = observacion;
    }

    @PrePersist
    protected void prePersist() {
        Instant ahora = Instant.now();

        if (fechaMovimiento == null) {
            fechaMovimiento = ahora;
        }

        fechaCreacion = ahora;
    }

    public UUID getId() {
        return id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Producto getProducto() {
        return producto;
    }

    public TipoMovimientoInventario getTipo() {
        return tipo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public Instant getFechaMovimiento() {
        return fechaMovimiento;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }
}