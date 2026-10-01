package cl.pymeflow.venta.service;

import cl.pymeflow.cliente.exception.ClienteNoEncontradoException;
import cl.pymeflow.cliente.model.Cliente;
import cl.pymeflow.cliente.repository.ClienteRepository;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.inventario.dto.RegistrarMovimientoRequest;
import cl.pymeflow.inventario.model.TipoMovimientoInventario;
import cl.pymeflow.inventario.service.InventarioService;
import cl.pymeflow.producto.exception.ProductoNoEncontradoException;
import cl.pymeflow.producto.model.Producto;
import cl.pymeflow.producto.repository.ProductoRepository;

import cl.pymeflow.security.UsuarioAutenticadoService;
import cl.pymeflow.venta.dto.CrearDetalleVentaRequest;
import cl.pymeflow.venta.dto.CrearVentaRequest;
import cl.pymeflow.venta.dto.VentaResponse;
import cl.pymeflow.venta.exception.VentaNoEncontradaException;
import cl.pymeflow.venta.model.DetalleVenta;
import cl.pymeflow.venta.model.Venta;
import cl.pymeflow.venta.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final InventarioService inventarioService;

    public VentaService(
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            InventarioService inventarioService
    ) {
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.inventarioService = inventarioService;
    }

    @Transactional
    public VentaResponse crear(CrearVentaRequest request) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        Cliente cliente = clienteRepository
                .findByIdAndEmpresaId(
                        request.clienteId(),
                        empresaId
                )
                .orElseThrow(() ->
                        new ClienteNoEncontradoException(
                                request.clienteId()
                        )
                );

        Empresa empresa = cliente.getEmpresa();

        Venta venta = new Venta(
                empresa,
                cliente,
                request.numeroDocumento(),
                request.fechaVenta(),
                request.observacion()
        );

        for (CrearDetalleVentaRequest detalleRequest
                : request.detalles()) {

            Producto producto = productoRepository
                    .findByIdAndEmpresaId(
                            detalleRequest.productoId(),
                            empresaId
                    )
                    .orElseThrow(() ->
                            new ProductoNoEncontradoException(
                                    detalleRequest.productoId()
                            )
                    );

            DetalleVenta detalle = new DetalleVenta(
                    producto,
                    detalleRequest.cantidad(),
                    detalleRequest.precioUnitario()
            );

            venta.agregarDetalle(detalle);
        }

        Venta guardada = ventaRepository.save(venta);

        return convertirAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return ventaRepository
                .findAllByEmpresaIdOrderByFechaVentaDesc(
                        empresaId
                )
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse buscarPorId(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        Venta venta = buscarEntidadPorId(
                id,
                empresaId
        );

        return convertirAResponse(venta);
    }

    @Transactional
    public VentaResponse confirmar(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        Venta venta = buscarEntidadPorId(
                id,
                empresaId
        );

        venta.confirmar();

        for (DetalleVenta detalle : venta.getDetalles()) {

            RegistrarMovimientoRequest movimiento =
                    new RegistrarMovimientoRequest(
                            TipoMovimientoInventario.SALIDA_VENTA,
                            detalle.getCantidad(),
                            "Salida por venta " + venta.getId()
                    );

            inventarioService.registrarMovimiento(
                    detalle.getProducto().getId(),
                    movimiento
            );
        }

        return convertirAResponse(venta);
    }

    private Venta buscarEntidadPorId(
            UUID id,
            UUID empresaId
    ) {
        return ventaRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new VentaNoEncontradaException(id)
                );
    }

    private VentaResponse convertirAResponse(
            Venta venta
    ) {

        List<VentaResponse.DetalleResponse> detalles =
                venta.getDetalles()
                        .stream()
                        .map(detalle ->
                                new VentaResponse.DetalleResponse(
                                        detalle.getId(),
                                        detalle.getProducto().getId(),
                                        detalle.getProducto().getNombre(),
                                        detalle.getCantidad(),
                                        detalle.getPrecioUnitario(),
                                        detalle.getSubtotal()
                                )
                        )
                        .toList();

        return new VentaResponse(
                venta.getId(),
                venta.getCliente().getId(),
                venta.getCliente().getRazonSocial(),
                venta.getNumeroDocumento(),
                venta.getFechaVenta(),
                venta.getEstado(),
                venta.getTotal(),
                venta.getObservacion(),
                detalles,
                venta.getFechaCreacion(),
                venta.getFechaActualizacion()
        );
    }
}