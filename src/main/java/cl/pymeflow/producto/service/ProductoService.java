package cl.pymeflow.producto.service;

import cl.pymeflow.categoria.model.Categoria;
import cl.pymeflow.categoria.service.CategoriaService;
import cl.pymeflow.empresa.model.Empresa;
import cl.pymeflow.producto.dto.ActualizarProductoRequest;
import cl.pymeflow.producto.dto.CrearProductoRequest;
import cl.pymeflow.producto.dto.ProductoResponse;
import cl.pymeflow.producto.exception.ProductoNoEncontradoException;
import cl.pymeflow.producto.exception.SkuProductoDuplicadoException;
import cl.pymeflow.producto.model.Producto;
import cl.pymeflow.producto.repository.ProductoRepository;
import cl.pymeflow.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaService categoriaService,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public ProductoResponse crear(CrearProductoRequest request) {

        Empresa empresa =
                usuarioAutenticadoService.obtenerUsuarioActual()
                        .getEmpresa();

        UUID empresaId = empresa.getId();

        if (productoRepository.existsByEmpresaIdAndSku(
                empresaId,
                request.sku()
        )) {
            throw new SkuProductoDuplicadoException(
                    request.sku()
            );
        }

        Categoria categoria =
                categoriaService.buscarEntidadPorId(
                        request.categoriaId()
                );

        Producto producto = new Producto(
                empresa,
                categoria,
                request.sku(),
                request.nombre(),
                request.descripcion(),
                request.precioCompra(),
                request.precioVenta(),
                request.unidadMedida()
        );

        Producto guardado =
                productoRepository.save(producto);

        return convertirAResponse(guardado);
    }

    public List<ProductoResponse> listar() {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return productoRepository
                .findAllByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ProductoResponse buscarPorId(UUID id) {
        return convertirAResponse(
                buscarEntidadPorId(id)
        );
    }

    @Transactional
    public ProductoResponse actualizar(
            UUID id,
            ActualizarProductoRequest request
    ) {

        Producto producto =
                buscarEntidadPorId(id);

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        if (productoRepository
                .existsByEmpresaIdAndSkuAndIdNot(
                        empresaId,
                        request.sku(),
                        id
                )) {
            throw new SkuProductoDuplicadoException(
                    request.sku()
            );
        }

        Categoria categoria =
                categoriaService.buscarEntidadPorId(
                        request.categoriaId()
                );

        producto.actualizarDatos(
                categoria,
                request.sku(),
                request.nombre(),
                request.descripcion(),
                request.precioCompra(),
                request.precioVenta(),
                request.unidadMedida()
        );

        return convertirAResponse(producto);
    }

    @Transactional
    public ProductoResponse activar(UUID id) {

        Producto producto =
                buscarEntidadPorId(id);

        producto.activar();

        return convertirAResponse(producto);
    }

    @Transactional
    public ProductoResponse desactivar(UUID id) {

        Producto producto =
                buscarEntidadPorId(id);

        producto.desactivar();

        return convertirAResponse(producto);
    }

    @Transactional
    public ProductoResponse descontinuar(UUID id) {

        Producto producto =
                buscarEntidadPorId(id);

        producto.descontinuar();

        return convertirAResponse(producto);
    }

    private Producto buscarEntidadPorId(UUID id) {

        UUID empresaId =
                usuarioAutenticadoService.obtenerEmpresaId();

        return productoRepository
                .findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() ->
                        new ProductoNoEncontradoException(id)
                );
    }

    private ProductoResponse convertirAResponse(
            Producto producto
    ) {

        return new ProductoResponse(
                producto.getId(),
                producto.getEmpresa().getId(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getSku(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecioCompra(),
                producto.getPrecioVenta(),
                producto.getUnidadMedida(),
                producto.getEstado(),
                producto.getFechaCreacion(),
                producto.getFechaActualizacion()
        );
    }
}