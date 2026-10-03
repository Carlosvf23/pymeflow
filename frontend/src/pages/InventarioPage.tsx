import {
    useEffect,
    useMemo,
    useState,
    type FormEvent
} from 'react'

import {
    obtenerProductos,
    type Producto
} from '../services/productoService'

import {
    obtenerMovimientos,
    obtenerStock,
    registrarMovimiento,
    type MovimientoInventario,
    type RegistrarMovimientoRequest
} from '../services/inventarioService'

import './InventarioPage.css'

interface ProductoConStock extends Producto {
    stock: number
}

const movimientoInicial: RegistrarMovimientoRequest = {
    tipo: 'AJUSTE_POSITIVO',
    cantidad: 1,
    observacion: ''
}

function InventarioPage() {
    const [productos, setProductos] =
        useState<ProductoConStock[]>([])

    const [busqueda, setBusqueda] = useState('')
    const [cargando, setCargando] = useState(true)
    const [error, setError] = useState('')

    const [productoSeleccionado, setProductoSeleccionado] =
        useState<ProductoConStock | null>(null)

    const [movimientos, setMovimientos] =
        useState<MovimientoInventario[]>([])

    const [cargandoMovimientos, setCargandoMovimientos] =
        useState(false)

    const [mostrarMovimiento, setMostrarMovimiento] =
        useState(false)

    const [guardando, setGuardando] =
        useState(false)

    const [tieneMovimientos, setTieneMovimientos] =
        useState(false)

    const [formulario, setFormulario] =
        useState<RegistrarMovimientoRequest>(
            movimientoInicial
        )

    useEffect(() => {
        cargarInventario()
    }, [])

    async function cargarInventario() {
        try {
            setCargando(true)
            setError('')

            const productosRespuesta =
                await obtenerProductos()

            const productosConStock =
                await Promise.all(
                    productosRespuesta.map(
                        async (producto) => {
                            const stock =
                                await obtenerStock(producto.id)

                            return {
                                ...producto,
                                stock: stock.stock
                            }
                        }
                    )
                )

            setProductos(productosConStock)

        } catch {
            setError(
                'No fue posible cargar el inventario.'
            )
        } finally {
            setCargando(false)
        }
    }

    async function verMovimientos(
        producto: ProductoConStock
    ) {
        try {
            setProductoSeleccionado(producto)
            setCargandoMovimientos(true)
            setError('')

            const datos =
                await obtenerMovimientos(producto.id)

            setMovimientos(datos)

        } catch {
            setError(
                'No fue posible cargar los movimientos.'
            )
        } finally {
            setCargandoMovimientos(false)
        }
    }

    function cerrarMovimientos() {
        setProductoSeleccionado(null)
        setMovimientos([])
        setError('')
    }

    async function abrirMovimiento(
        producto: ProductoConStock
    ) {
        try {
            setError('')

            /*
             * No basta con revisar si el stock es 0.
             * Un producto puede haber tenido movimientos y
             * haber vuelto posteriormente a stock 0.
             *
             * Por eso consultamos su historial.
             */
            const movimientosProducto =
                await obtenerMovimientos(producto.id)

            const existenMovimientos =
                movimientosProducto.length > 0

            setTieneMovimientos(existenMovimientos)
            setProductoSeleccionado(producto)

            setFormulario({
                tipo: existenMovimientos
                    ? 'AJUSTE_POSITIVO'
                    : 'ENTRADA_INICIAL',
                cantidad: 1,
                observacion: ''
            })

            setMostrarMovimiento(true)

        } catch {
            setError(
                'No fue posible comprobar el historial del producto.'
            )
        }
    }

    function cerrarMovimiento() {
        if (guardando) return

        setMostrarMovimiento(false)
        setProductoSeleccionado(null)
        setTieneMovimientos(false)
        setFormulario(movimientoInicial)
        setError('')
    }

    async function guardarMovimiento(
        e: FormEvent<HTMLFormElement>
    ) {
        e.preventDefault()

        if (!productoSeleccionado) return

        try {
            setGuardando(true)
            setError('')

            await registrarMovimiento(
                productoSeleccionado.id,
                formulario
            )

            await cargarInventario()

            setMostrarMovimiento(false)
            setProductoSeleccionado(null)
            setTieneMovimientos(false)
            setFormulario(movimientoInicial)

        } catch {
            setError(
                'No fue posible registrar el movimiento. Si es un ajuste negativo, revisa que exista stock suficiente.'
            )
        } finally {
            setGuardando(false)
        }
    }

    const productosFiltrados = useMemo(() => {
        const texto =
            busqueda.trim().toLowerCase()

        if (!texto) return productos

        return productos.filter((producto) =>
            producto.nombre
                .toLowerCase()
                .includes(texto) ||
            producto.sku
                .toLowerCase()
                .includes(texto) ||
            producto.categoriaNombre
                .toLowerCase()
                .includes(texto)
        )
    }, [productos, busqueda])

    function nombreMovimiento(
        tipo: string
    ): string {
        switch (tipo) {
            case 'ENTRADA_INICIAL':
                return 'Entrada inicial'

            case 'ENTRADA_COMPRA':
                return 'Compra'

            case 'SALIDA_VENTA':
                return 'Venta'

            case 'AJUSTE_POSITIVO':
                return 'Ajuste +'

            case 'AJUSTE_NEGATIVO':
                return 'Ajuste -'

            default:
                return tipo
        }
    }

    function esEntrada(tipo: string) {
        return (
            tipo === 'ENTRADA_INICIAL' ||
            tipo === 'ENTRADA_COMPRA' ||
            tipo === 'AJUSTE_POSITIVO'
        )
    }

    const fecha = new Intl.DateTimeFormat(
        'es-CL',
        {
            dateStyle: 'short',
            timeStyle: 'short'
        }
    )

    if (cargando) {
        return (
            <div className="inventario-mensaje">
                Cargando inventario...
            </div>
        )
    }

    return (
        <main className="inventario-page">

            <header className="inventario-header">
                <div>
                    <h1>Inventario</h1>

                    <p>
                        Consulta stock y movimientos de productos
                    </p>
                </div>
            </header>

            <section className="inventario-resumen">

                <div className="inventario-card">
                    <span>Productos</span>
                    <strong>{productos.length}</strong>
                </div>

                <div className="inventario-card">
                    <span>Con stock</span>
                    <strong>
                        {
                            productos.filter(
                                (p) => p.stock > 0
                            ).length
                        }
                    </strong>
                </div>

                <div className="inventario-card">
                    <span>Sin stock</span>
                    <strong>
                        {
                            productos.filter(
                                (p) => p.stock === 0
                            ).length
                        }
                    </strong>
                </div>

            </section>

            <section className="inventario-panel">

                <div className="inventario-toolbar">

                    <input
                        className="inventario-buscador"
                        type="search"
                        placeholder="Buscar producto, SKU o categoría..."
                        value={busqueda}
                        onChange={(e) =>
                            setBusqueda(e.target.value)
                        }
                    />

                    <span>
                        {productosFiltrados.length}
                        {' '}
                        producto
                        {productosFiltrados.length !== 1
                            ? 's'
                            : ''}
                    </span>

                </div>

                {error &&
                    !mostrarMovimiento &&
                    !productoSeleccionado && (
                        <div className="inventario-error">
                            {error}
                        </div>
                    )}

                <div className="inventario-tabla-contenedor">

                    <table className="inventario-tabla">

                        <thead>
                        <tr>
                            <th>SKU</th>
                            <th>Producto</th>
                            <th>Categoría</th>
                            <th>Unidad</th>
                            <th>Stock actual</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>

                        <tbody>

                        {productosFiltrados.map(
                            (producto) => (

                                <tr key={producto.id}>

                                    <td>
                                        <strong>
                                            {producto.sku}
                                        </strong>
                                    </td>

                                    <td>
                                        {producto.nombre}
                                    </td>

                                    <td>
                                        {
                                            producto.categoriaNombre
                                        }
                                    </td>

                                    <td>
                                        {
                                            producto.unidadMedida
                                        }
                                    </td>

                                    <td>
                                            <span
                                                className={
                                                    producto.stock > 0
                                                        ? 'inventario-stock inventario-stock-ok'
                                                        : 'inventario-stock inventario-stock-cero'
                                                }
                                            >
                                                {producto.stock}
                                            </span>
                                    </td>

                                    <td>
                                        <div className="inventario-acciones">

                                            <button
                                                className="inventario-boton-historial"
                                                type="button"
                                                onClick={() =>
                                                    verMovimientos(
                                                        producto
                                                    )
                                                }
                                            >
                                                Movimientos
                                            </button>

                                            <button
                                                className="inventario-boton-ajuste"
                                                type="button"
                                                onClick={() =>
                                                    abrirMovimiento(
                                                        producto
                                                    )
                                                }
                                            >
                                                Ajustar stock
                                            </button>

                                        </div>
                                    </td>

                                </tr>
                            )
                        )}

                        </tbody>

                    </table>

                </div>

            </section>

            {productoSeleccionado &&
                !mostrarMovimiento && (

                    <div className="inventario-modal-fondo">

                        <div className="inventario-modal inventario-modal-grande">

                            <div className="inventario-modal-header">

                                <div>
                                    <h2>
                                        Movimientos
                                    </h2>

                                    <p>
                                        {
                                            productoSeleccionado.nombre
                                        }
                                        {' · '}
                                        {
                                            productoSeleccionado.sku
                                        }
                                    </p>
                                </div>

                                <button
                                    type="button"
                                    onClick={
                                        cerrarMovimientos
                                    }
                                >
                                    ×
                                </button>

                            </div>

                            <div className="inventario-modal-contenido">

                                {cargandoMovimientos ? (

                                    <p>
                                        Cargando movimientos...
                                    </p>

                                ) : movimientos.length === 0 ? (

                                    <div className="inventario-sin-movimientos">
                                        Este producto aún no tiene movimientos.
                                    </div>

                                ) : (

                                    <table className="inventario-tabla">

                                        <thead>
                                        <tr>
                                            <th>Fecha</th>
                                            <th>Tipo</th>
                                            <th>Cantidad</th>
                                            <th>Observación</th>
                                        </tr>
                                        </thead>

                                        <tbody>

                                        {movimientos.map(
                                            (movimiento) => (

                                                <tr
                                                    key={
                                                        movimiento.id
                                                    }
                                                >

                                                    <td>
                                                        {
                                                            fecha.format(
                                                                new Date(
                                                                    movimiento.fechaMovimiento
                                                                )
                                                            )
                                                        }
                                                    </td>

                                                    <td>
                                                        {
                                                            nombreMovimiento(
                                                                movimiento.tipo
                                                            )
                                                        }
                                                    </td>

                                                    <td>
                                                        <strong
                                                            className={
                                                                esEntrada(
                                                                    movimiento.tipo
                                                                )
                                                                    ? 'inventario-cantidad-positiva'
                                                                    : 'inventario-cantidad-negativa'
                                                            }
                                                        >
                                                            {
                                                                esEntrada(
                                                                    movimiento.tipo
                                                                )
                                                                    ? '+'
                                                                    : '-'
                                                            }
                                                            {
                                                                movimiento.cantidad
                                                            }
                                                        </strong>
                                                    </td>

                                                    <td>
                                                        {
                                                            movimiento.observacion ||
                                                            '-'
                                                        }
                                                    </td>

                                                </tr>
                                            )
                                        )}

                                        </tbody>

                                    </table>
                                )}

                            </div>

                        </div>

                    </div>
                )}

            {mostrarMovimiento &&
                productoSeleccionado && (

                    <div className="inventario-modal-fondo">

                        <div className="inventario-modal">

                            <div className="inventario-modal-header">

                                <div>
                                    <h2>
                                        Ajustar stock
                                    </h2>

                                    <p>
                                        {
                                            productoSeleccionado.nombre
                                        }
                                        {' · Stock actual: '}
                                        {
                                            productoSeleccionado.stock
                                        }
                                    </p>
                                </div>

                                <button
                                    type="button"
                                    onClick={
                                        cerrarMovimiento
                                    }
                                >
                                    ×
                                </button>

                            </div>

                            <form
                                className="inventario-form"
                                onSubmit={
                                    guardarMovimiento
                                }
                            >

                                <label>
                                    Tipo de movimiento *

                                    <select
                                        required
                                        value={
                                            formulario.tipo
                                        }
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                tipo:
                                                    e.target.value as RegistrarMovimientoRequest['tipo']
                                            })
                                        }
                                    >

                                        {!tieneMovimientos && (
                                            <option value="ENTRADA_INICIAL">
                                                Entrada inicial
                                            </option>
                                        )}

                                        <option value="AJUSTE_POSITIVO">
                                            Ajuste positivo
                                        </option>

                                        <option value="AJUSTE_NEGATIVO">
                                            Ajuste negativo
                                        </option>

                                    </select>
                                </label>

                                <label>
                                    Cantidad *

                                    <input
                                        required
                                        type="number"
                                        min="0.001"
                                        step="0.001"
                                        value={
                                            formulario.cantidad
                                        }
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                cantidad:
                                                    Number(
                                                        e.target.value
                                                    )
                                            })
                                        }
                                    />
                                </label>

                                <label>
                                    Observación

                                    <textarea
                                        maxLength={500}
                                        value={
                                            formulario.observacion
                                        }
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                observacion:
                                                e.target.value
                                            })
                                        }
                                        placeholder="Motivo del ajuste..."
                                    />
                                </label>

                                {error && (
                                    <div className="inventario-error">
                                        {error}
                                    </div>
                                )}

                                <div className="inventario-modal-acciones">

                                    <button
                                        type="button"
                                        className="inventario-boton-secundario"
                                        onClick={
                                            cerrarMovimiento
                                        }
                                    >
                                        Cancelar
                                    </button>

                                    <button
                                        type="submit"
                                        className="inventario-boton-principal"
                                        disabled={guardando}
                                    >
                                        {guardando
                                            ? 'Registrando...'
                                            : 'Registrar movimiento'}
                                    </button>

                                </div>

                            </form>

                        </div>

                    </div>
                )}

        </main>
    )
}

export default InventarioPage