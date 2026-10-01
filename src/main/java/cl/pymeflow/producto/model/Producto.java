package cl.pymeflow.producto.model;

import cl.pymeflow.categoria.model.Categoria;
import cl.pymeflow.empresa.model.Empresa;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "precio_compra", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioVenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false, length = 30)
    private UnidadMedida unidadMedida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estado;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    protected Producto() {
    }

    public Producto(
            Empresa empresa,
            Categoria categoria,
            String sku,
            String nombre,
            String descripcion,
            BigDecimal precioCompra,
            BigDecimal precioVenta,
            UnidadMedida unidadMedida
    ) {
        this.id = UUID.randomUUID();
        this.empresa = empresa;
        this.categoria = categoria;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.unidadMedida = unidadMedida;
        this.estado = EstadoProducto.ACTIVO;
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
            Categoria categoria,
            String sku,
            String nombre,
            String descripcion,
            BigDecimal precioCompra,
            BigDecimal precioVenta,
            UnidadMedida unidadMedida
    ) {
        this.categoria = categoria;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.unidadMedida = unidadMedida;
    }

    public void activar() {
        this.estado = EstadoProducto.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoProducto.INACTIVO;
    }

    public void descontinuar() {
        this.estado = EstadoProducto.DESCONTINUADO;
    }

    public UUID getId() {
        return id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getSku() {
        return sku;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }
}