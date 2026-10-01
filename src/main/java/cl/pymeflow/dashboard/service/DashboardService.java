package cl.pymeflow.dashboard.service;

import cl.pymeflow.cliente.repository.ClienteRepository;
import cl.pymeflow.compra.model.Compra;
import cl.pymeflow.compra.model.EstadoCompra;
import cl.pymeflow.compra.repository.CompraRepository;
import cl.pymeflow.dashboard.dto.DashboardResponse;
import cl.pymeflow.producto.repository.ProductoRepository;
import cl.pymeflow.proveedor.repository.ProveedorRepository;

import cl.pymeflow.security.UsuarioAutenticadoService;
import cl.pymeflow.venta.model.EstadoVenta;
import cl.pymeflow.venta.model.Venta;
import cl.pymeflow.venta.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final VentaRepository ventaRepository;
    private final CompraRepository compraRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public DashboardService(
            VentaRepository ventaRepository,
            CompraRepository compraRepository,
            ClienteRepository clienteRepository,
            ProveedorRepository proveedorRepository,
            ProductoRepository productoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.ventaRepository = ventaRepository;
        this.compraRepository = compraRepository;
        this.clienteRepository = clienteRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse obtenerDashboard() {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        BigDecimal totalVentas =
                ventaRepository.sumarTotalPorEmpresaYEstado(
                        empresaId,
                        EstadoVenta.CONFIRMADA
                );

        BigDecimal totalCompras =
                compraRepository.sumarTotalPorEmpresaYEstado(
                        empresaId,
                        EstadoCompra.CONFIRMADA
                );

        long cantidadClientes =
                clienteRepository.countByEmpresaId(empresaId);

        long cantidadProveedores =
                proveedorRepository.countByEmpresaId(empresaId);

        long cantidadProductos =
                productoRepository.countByEmpresaId(empresaId);

        List<DashboardResponse.VentaResumen> ultimasVentas =
                ventaRepository
                        .findTop5ByEmpresaIdAndEstadoOrderByFechaVentaDesc(
                                empresaId,
                                EstadoVenta.CONFIRMADA
                        )
                        .stream()
                        .map(this::convertirVenta)
                        .toList();

        List<DashboardResponse.CompraResumen> ultimasCompras =
                compraRepository
                        .findTop5ByEmpresaIdAndEstadoOrderByFechaCompraDesc(
                                empresaId,
                                EstadoCompra.CONFIRMADA
                        )
                        .stream()
                        .map(this::convertirCompra)
                        .toList();

        return new DashboardResponse(
                totalVentas,
                totalCompras,
                cantidadClientes,
                cantidadProveedores,
                cantidadProductos,
                ultimasVentas,
                ultimasCompras
        );
    }

    private DashboardResponse.VentaResumen convertirVenta(
            Venta venta
    ) {
        return new DashboardResponse.VentaResumen(
                venta.getId(),
                venta.getNumeroDocumento(),
                venta.getCliente().getRazonSocial(),
                venta.getTotal(),
                venta.getFechaVenta()
        );
    }

    private DashboardResponse.CompraResumen convertirCompra(
            Compra compra
    ) {
        return new DashboardResponse.CompraResumen(
                compra.getId(),
                compra.getNumeroDocumento(),
                compra.getProveedor().getRazonSocial(),
                compra.getTotal(),
                compra.getFechaCompra()
        );
    }
}