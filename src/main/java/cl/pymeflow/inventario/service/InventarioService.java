package cl.pymeflow.inventario.service;

import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.inventario.exception.StockInsuficienteException;
import cl.pymeflow.inventario.model.MovimientoInventario;
import cl.pymeflow.inventario.model.TipoMovimientoInventario;
import cl.pymeflow.inventario.repository.MovimientoInventarioRepository;
import cl.pymeflow.producto.model.Producto;
import cl.pymeflow.producto.repository.ProductoRepository;
import cl.pymeflow.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.pymeflow.inventario.dto.MovimientoInventarioResponse;
import cl.pymeflow.inventario.dto.RegistrarMovimientoRequest;
import cl.pymeflow.inventario.dto.StockProductoResponse;
import cl.pymeflow.producto.exception.ProductoNoEncontradoException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class InventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public InventarioService(
            MovimientoInventarioRepository movimientoRepository,
            ProductoRepository productoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public StockProductoResponse obtenerStock(UUID productoId) {

        UUID empresaId = usuarioAutenticadoService.obtenerEmpresaId();

        buscarProducto(productoId, empresaId);

        BigDecimal stock = movimientoRepository.calcularStock(
                empresaId,
                productoId
        );

        return new StockProductoResponse(
                productoId,
                stock
        );
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarMovimientos(UUID productoId) {

        UUID empresaId = usuarioAutenticadoService.obtenerEmpresaId();

        buscarProducto(productoId, empresaId);

        return movimientoRepository
                .findAllByEmpresaIdAndProductoIdOrderByFechaMovimientoDesc(
                        empresaId,
                        productoId
                )
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public MovimientoInventarioResponse registrarMovimiento(
            UUID productoId,
            RegistrarMovimientoRequest request
    ) {

        UUID empresaId = usuarioAutenticadoService.obtenerEmpresaId();

        Producto producto = buscarProducto(
                productoId,
                empresaId
        );

        validarCantidad(request.cantidad());

        BigDecimal stockActual =
                movimientoRepository.calcularStock(
                        empresaId,
                        productoId
                );

        if (esSalida(request.tipo())
                && stockActual.compareTo(request.cantidad()) < 0) {

            throw new StockInsuficienteException(
                    stockActual,
                    request.cantidad()
            );
        }

        Empresa empresa = producto.getEmpresa();

        MovimientoInventario movimiento =
                new MovimientoInventario(
                        empresa,
                        producto,
                        request.tipo(),
                        request.cantidad(),
                        request.observacion()
                );

        MovimientoInventario guardado =
                movimientoRepository.save(movimiento);

        return convertirAResponse(guardado);
    }

    private MovimientoInventarioResponse convertirAResponse(
            MovimientoInventario movimiento
    ) {
        return new MovimientoInventarioResponse(
                movimiento.getId(),
                movimiento.getProducto().getId(),
                movimiento.getProducto().getNombre(),
                movimiento.getTipo(),
                movimiento.getCantidad(),
                movimiento.getObservacion(),
                movimiento.getFechaMovimiento()
        );
    }

    private Producto buscarProducto(
            UUID productoId,
            UUID empresaId
    ) {
        return productoRepository
                .findByIdAndEmpresaId(productoId, empresaId)
                .orElseThrow(() ->
                        new ProductoNoEncontradoException(productoId)
                );
    }

    private void validarCantidad(BigDecimal cantidad) {

        if (cantidad == null
                || cantidad.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }
    }

    private boolean esSalida(
            TipoMovimientoInventario tipo
    ) {
        return tipo == TipoMovimientoInventario.SALIDA_VENTA
                || tipo == TipoMovimientoInventario.AJUSTE_NEGATIVO;
    }
}