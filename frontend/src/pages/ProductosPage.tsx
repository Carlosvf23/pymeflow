import {
    useEffect,
    useMemo,
    useState,
    type FormEvent
} from 'react'

import {
    activarProducto,
    actualizarProducto,
    crearProducto,
    desactivarProducto,
    descontinuarProducto,
    obtenerProductos,
    type Producto,
    type ProductoRequest
} from '../services/productoService'

import {
    obtenerCategorias,
    type Categoria
} from '../services/categoriaService'

import './ProductosPage.css'

const formularioInicial: ProductoRequest = {
    categoriaId: '',
    sku: '',
    nombre: '',
    descripcion: '',
    precioCompra: 0,
    precioVenta: 0,
    unidadMedida: 'UNIDAD'
}

function ProductosPage() {
    const [productos, setProductos] = useState<Producto[]>([])
    const [categorias, setCategorias] = useState<Categoria[]>([])
    const [busqueda, setBusqueda] = useState('')
    const [cargando, setCargando] = useState(true)
    const [guardando, setGuardando] = useState(false)
    const [error, setError] = useState('')
    const [mostrarFormulario, setMostrarFormulario] = useState(false)
    const [productoEditando, setProductoEditando] =
        useState<Producto | null>(null)

    const [formulario, setFormulario] =
        useState<ProductoRequest>(formularioInicial)

    useEffect(() => {
        cargarDatos()
    }, [])

    async function cargarDatos() {
        try {
            setCargando(true)
            setError('')

            const [productosRespuesta, categoriasRespuesta] =
                await Promise.all([
                    obtenerProductos(),
                    obtenerCategorias()
                ])

            setProductos(productosRespuesta)
            setCategorias(categoriasRespuesta)
        } catch {
            setError('No fue posible cargar los productos.')
        } finally {
            setCargando(false)
        }
    }

    function abrirNuevoProducto() {
        setProductoEditando(null)
        setFormulario(formularioInicial)
        setError('')
        setMostrarFormulario(true)
    }

    function abrirEditarProducto(producto: Producto) {
        setProductoEditando(producto)

        setFormulario({
            categoriaId: producto.categoriaId,
            sku: producto.sku,
            nombre: producto.nombre,
            descripcion: producto.descripcion ?? '',
            precioCompra: producto.precioCompra,
            precioVenta: producto.precioVenta,
            unidadMedida: producto.unidadMedida
        })

        setError('')
        setMostrarFormulario(true)
    }

    function cerrarFormulario() {
        if (guardando) return

        setMostrarFormulario(false)
        setProductoEditando(null)
        setFormulario(formularioInicial)
        setError('')
    }

    async function guardarProducto(e: FormEvent<HTMLFormElement>) {
        e.preventDefault()

        try {
            setGuardando(true)
            setError('')

            if (productoEditando) {
                await actualizarProducto(
                    productoEditando.id,
                    formulario
                )
            } else {
                await crearProducto(formulario)
            }

            await cargarDatos()
            cerrarFormulario()
        } catch {
            setError(
                'No fue posible guardar el producto. Revisa SKU, categoría y datos ingresados.'
            )
        } finally {
            setGuardando(false)
        }
    }

    async function cambiarEstado(
        producto: Producto,
        accion: 'activar' | 'desactivar' | 'descontinuar'
    ) {
        try {
            setError('')

            if (accion === 'activar') {
                await activarProducto(producto.id)
            }

            if (accion === 'desactivar') {
                await desactivarProducto(producto.id)
            }

            if (accion === 'descontinuar') {
                await descontinuarProducto(producto.id)
            }

            await cargarDatos()
        } catch {
            setError('No fue posible cambiar el estado del producto.')
        }
    }

    const productosFiltrados = useMemo(() => {
        const texto = busqueda.trim().toLowerCase()

        if (!texto) return productos

        return productos.filter((producto) =>
            producto.nombre.toLowerCase().includes(texto) ||
            producto.sku.toLowerCase().includes(texto) ||
            producto.categoriaNombre.toLowerCase().includes(texto)
        )
    }, [productos, busqueda])

    const dinero = new Intl.NumberFormat('es-CL', {
        style: 'currency',
        currency: 'CLP',
        maximumFractionDigits: 0
    })

    if (cargando) {
        return <div className="productos-mensaje">Cargando productos...</div>
    }

    return (
        <main className="productos-page">

            <header className="productos-header">
                <div>
                    <h1>Productos</h1>
                    <p>Administra el catálogo de productos de tu empresa</p>
                </div>

                <div className="productos-header-acciones">
                    <button
                        className="producto-boton-secundario"
                        type="button"
                        onClick={() => {
                            window.location.href = '/categorias'
                        }}
                    >
                        Categorías
                    </button>

                    <button
                        className="producto-boton-principal"
                        type="button"
                        onClick={abrirNuevoProducto}
                    >
                        + Nuevo producto
                    </button>
                </div>
            </header>

            <section className="productos-panel">

                <div className="productos-toolbar">
                    <input
                        className="productos-buscador"
                        type="search"
                        placeholder="Buscar por producto, SKU o categoría..."
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />

                    <span>
                        {productosFiltrados.length} producto
                        {productosFiltrados.length !== 1 ? 's' : ''}
                    </span>
                </div>

                {error && !mostrarFormulario && (
                    <div className="productos-error">{error}</div>
                )}

                <div className="productos-tabla-contenedor">
                    <table className="productos-tabla">

                        <thead>
                        <tr>
                            <th>SKU</th>
                            <th>Producto</th>
                            <th>Categoría</th>
                            <th>Compra</th>
                            <th>Venta</th>
                            <th>Unidad</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>

                        <tbody>
                        {productosFiltrados.map((producto) => (
                            <tr key={producto.id}>

                                <td>
                                    <strong>{producto.sku}</strong>
                                </td>

                                <td>
                                    <div className="producto-nombre">
                                        <strong>{producto.nombre}</strong>
                                        <small>
                                            {producto.descripcion || ''}
                                        </small>
                                    </div>
                                </td>

                                <td>{producto.categoriaNombre}</td>

                                <td>
                                    {dinero.format(producto.precioCompra)}
                                </td>

                                <td>
                                    {dinero.format(producto.precioVenta)}
                                </td>

                                <td>{producto.unidadMedida}</td>

                                <td>
                                        <span
                                            className={`producto-estado producto-${producto.estado.toLowerCase()}`}
                                        >
                                            {producto.estado}
                                        </span>
                                </td>

                                <td>
                                    <div className="producto-acciones">

                                        <button
                                            className="producto-editar"
                                            onClick={() =>
                                                abrirEditarProducto(producto)
                                            }
                                        >
                                            Editar
                                        </button>

                                        {producto.estado === 'ACTIVO' && (
                                            <>
                                                <button
                                                    className="producto-desactivar"
                                                    onClick={() =>
                                                        cambiarEstado(
                                                            producto,
                                                            'desactivar'
                                                        )
                                                    }
                                                >
                                                    Desactivar
                                                </button>

                                                <button
                                                    className="producto-descontinuar"
                                                    onClick={() =>
                                                        cambiarEstado(
                                                            producto,
                                                            'descontinuar'
                                                        )
                                                    }
                                                >
                                                    Descontinuar
                                                </button>
                                            </>
                                        )}

                                        {producto.estado === 'INACTIVO' && (
                                            <button
                                                className="producto-activar"
                                                onClick={() =>
                                                    cambiarEstado(
                                                        producto,
                                                        'activar'
                                                    )
                                                }
                                            >
                                                Activar
                                            </button>
                                        )}

                                        {producto.estado ===
                                            'DESCONTINUADO' && (
                                                <button
                                                    className="producto-activar"
                                                    onClick={() =>
                                                        cambiarEstado(
                                                            producto,
                                                            'activar'
                                                        )
                                                    }
                                                >
                                                    Reactivar
                                                </button>
                                            )}

                                    </div>
                                </td>

                            </tr>
                        ))}
                        </tbody>

                    </table>
                </div>

            </section>

            {mostrarFormulario && (
                <div className="producto-modal-fondo">

                    <div className="producto-modal">

                        <div className="producto-modal-header">
                            <div>
                                <h2>
                                    {productoEditando
                                        ? 'Editar producto'
                                        : 'Nuevo producto'}
                                </h2>

                                <p>
                                    Completa la información del producto
                                </p>
                            </div>

                            <button onClick={cerrarFormulario}>
                                ×
                            </button>
                        </div>

                        <form onSubmit={guardarProducto}>

                            <div className="producto-form-grid">

                                <label>
                                    Categoría *

                                    <select
                                        required
                                        value={formulario.categoriaId}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                categoriaId: e.target.value
                                            })
                                        }
                                    >
                                        <option value="">
                                            Seleccionar categoría
                                        </option>

                                        {categorias
                                            .filter(
                                                (categoria) =>
                                                    categoria.estado === 'ACTIVA'
                                            )
                                            .map((categoria) => (
                                                <option
                                                    key={categoria.id}
                                                    value={categoria.id}
                                                >
                                                    {categoria.nombre}
                                                </option>
                                            ))}
                                    </select>
                                </label>

                                <label>
                                    SKU *

                                    <input
                                        required
                                        maxLength={50}
                                        value={formulario.sku}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                sku: e.target.value
                                            })
                                        }
                                    />
                                </label>

                                <label className="producto-form-ancho">
                                    Nombre *

                                    <input
                                        required
                                        maxLength={150}
                                        value={formulario.nombre}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                nombre: e.target.value
                                            })
                                        }
                                    />
                                </label>

                                <label>
                                    Precio compra *

                                    <input
                                        required
                                        type="number"
                                        min="0"
                                        step="1"
                                        value={formulario.precioCompra}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                precioCompra:
                                                    Number(e.target.value)
                                            })
                                        }
                                    />
                                </label>

                                <label>
                                    Precio venta *

                                    <input
                                        required
                                        type="number"
                                        min="0"
                                        step="1"
                                        value={formulario.precioVenta}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                precioVenta:
                                                    Number(e.target.value)
                                            })
                                        }
                                    />
                                </label>

                                <label>
                                    Unidad de medida *

                                    <select
                                        required
                                        value={formulario.unidadMedida}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                unidadMedida: e.target.value
                                            })
                                        }
                                    >
                                        <option value="UNIDAD">Unidad</option>
                                        <option value="KILOGRAMO">Kilogramo</option>
                                        <option value="GRAMO">Gramo</option>
                                        <option value="LITRO">Litro</option>
                                        <option value="MILILITRO">Mililitro</option>
                                        <option value="METRO">Metro</option>
                                        <option value="CENTIMETRO">Centímetro</option>
                                        <option value="CAJA">Caja</option>
                                        <option value="PACK">Pack</option>
                                    </select>
                                </label>

                                <label className="producto-form-ancho">
                                    Descripción

                                    <textarea
                                        maxLength={500}
                                        value={formulario.descripcion}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                descripcion: e.target.value
                                            })
                                        }
                                    />
                                </label>

                            </div>

                            {error && (
                                <div className="productos-error">
                                    {error}
                                </div>
                            )}

                            <div className="producto-modal-acciones">

                                <button
                                    type="button"
                                    className="producto-boton-secundario"
                                    onClick={cerrarFormulario}
                                >
                                    Cancelar
                                </button>

                                <button
                                    className="producto-boton-principal"
                                    disabled={guardando}
                                >
                                    {guardando
                                        ? 'Guardando...'
                                        : productoEditando
                                            ? 'Guardar cambios'
                                            : 'Crear producto'}
                                </button>

                            </div>

                        </form>

                    </div>
                </div>
            )}

        </main>
    )
}

export default ProductosPage