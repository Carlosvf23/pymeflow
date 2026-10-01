package cl.pymeflow.compra.service;

import cl.pymeflow.compra.dto.CompraResponse;
import cl.pymeflow.compra.dto.CrearCompraRequest;
import cl.pymeflow.compra.dto.CrearDetalleCompraRequest;
import cl.pymeflow.compra.exception.CompraNoEncontradaException;
import cl.pymeflow.compra.model.Compra;
import cl.pymeflow.compra.model.DetalleCompra;
import cl.pymeflow.compra.repository.CompraRepository;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.producto.exception.ProductoNoEncontradoException;
import cl.pymeflow.producto.model.Producto;
import cl.pymeflow.producto.repository.ProductoRepository;
import cl.pymeflow.proveedor.exception.ProveedorNoEncontradoException;
import cl.pymeflow.proveedor.model.Proveedor;
import cl.pymeflow.proveedor.repository.ProveedorRepository;
import cl.pymeflow.inventario.dto.RegistrarMovimientoRequest;
import cl.pymeflow.inventario.model.TipoMovimientoInventario;
import cl.pymeflow.inventario.service.InventarioService;

import cl.pymeflow.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final InventarioService inventarioService;

    public CompraService(
            CompraRepository compraRepository,
            ProveedorRepository proveedorRepository,
            ProductoRepository productoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            InventarioService inventarioService
    ) {
        this.compraRepository = compraRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.inventarioService = inventarioService;
    }

    @Transactional
    public CompraResponse crear(CrearCompraRequest request) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        Proveedor proveedor = proveedorRepository
                .findByIdAndEmpresaId(
                        request.proveedorId(),
                        empresaId
                )
                .orElseThrow(() ->
                        new ProveedorNoEncontradoException(
                                request.proveedorId()
                        )
                );

        Empresa empresa = proveedor.getEmpresa();

        Compra compra = new Compra(
                empresa,
                proveedor,
                request.numeroDocumento(),
                request.fechaCompra(),
                request.observacion()
        );

        for (CrearDetalleCompraRequest detalleRequest
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

            DetalleCompra detalle = new DetalleCompra(
                    producto,
                    detalleRequest.cantidad(),
                    detalleRequest.precioUnitario()
            );

            compra.agregarDetalle(detalle);
        }

        Compra guardada = compraRepository.save(compra);

        return convertirAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> listar() {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return compraRepository
                .findAllByEmpresaIdOrderByFechaCompraDesc(
                        empresaId
                )
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompraResponse buscarPorId(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        Compra compra = buscarEntidadPorId(
                id,
                empresaId
        );

        return convertirAResponse(compra);
    }
    @Transactional
    public CompraResponse confirmar(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        Compra compra = buscarEntidadPorId(
                id,
                empresaId
        );

        compra.confirmar();

        for (DetalleCompra detalle : compra.getDetalles()) {

            RegistrarMovimientoRequest movimiento =
                    new RegistrarMovimientoRequest(
                            TipoMovimientoInventario.ENTRADA_COMPRA,
                            detalle.getCantidad(),
                            "Entrada por compra " + compra.getId()
                    );

            inventarioService.registrarMovimiento(
                    detalle.getProducto().getId(),
                    movimiento
            );
        }

        return convertirAResponse(compra);
    }

    private Compra buscarEntidadPorId(
            UUID id,
            UUID empresaId
    ) {
        return compraRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new CompraNoEncontradaException(id)
                );
    }

    private CompraResponse convertirAResponse(
            Compra compra
    ) {

        List<CompraResponse.DetalleResponse> detalles =
                compra.getDetalles()
                        .stream()
                        .map(detalle ->
                                new CompraResponse.DetalleResponse(
                                        detalle.getId(),
                                        detalle.getProducto().getId(),
                                        detalle.getProducto().getNombre(),
                                        detalle.getCantidad(),
                                        detalle.getPrecioUnitario(),
                                        detalle.getSubtotal()
                                )
                        )
                        .toList();

        return new CompraResponse(
                compra.getId(),
                compra.getProveedor().getId(),
                compra.getProveedor().getRazonSocial(),
                compra.getNumeroDocumento(),
                compra.getFechaCompra(),
                compra.getEstado(),
                compra.getTotal(),
                compra.getObservacion(),
                detalles,
                compra.getFechaCreacion(),
                compra.getFechaActualizacion()
        );
    }
}