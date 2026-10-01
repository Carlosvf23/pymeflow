package cl.pymeflow.compra.model;

import cl.pymeflow.producto.model.Producto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "compra_detalles")
public class DetalleCompra {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    protected DetalleCompra() {
    }

    public DetalleCompra(
            Producto producto,
            BigDecimal cantidad,
            BigDecimal precioUnitario
    ) {
        this.id = UUID.randomUUID();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;

        this.subtotal = cantidad.multiply(precioUnitario);
    }

    void asignarCompra(Compra compra) {
        this.compra = compra;
    }

    public UUID getId() {
        return id;
    }

    public Compra getCompra() {
        return compra;
    }

    public Producto getProducto() {
        return producto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}