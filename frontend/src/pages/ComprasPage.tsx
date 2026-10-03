import {
    useEffect,
    useMemo,
    useState,
    type FormEvent
} from 'react'

import {
    obtenerCompras,
    crearCompra,
    confirmarCompra,
    type Compra
} from '../services/compraService'

import {
    obtenerProveedores,
    type Proveedor
} from '../services/proveedorService'

import {
    obtenerProductos,
    type Producto
} from '../services/productoService'

import './ComprasPage.css'

interface DetalleFormulario {
    idTemporal: number
    productoId: string
    cantidad: number | ''
    precioUnitario: number | ''
}

function fechaLocalParaInput(): string {
    const ahora = new Date()

    const offset =
        ahora.getTimezoneOffset() * 60000

    return new Date(
        ahora.getTime() - offset
    )
        .toISOString()
        .slice(0, 16)
}

function ComprasPage() {
    const [compras, setCompras] =
        useState<Compra[]>([])

    const [proveedores, setProveedores] =
        useState<Proveedor[]>([])

    const [productos, setProductos] =
        useState<Producto[]>([])

    const [busqueda, setBusqueda] =
        useState('')

    const [cargando, setCargando] =
        useState(true)

    const [guardando, setGuardando] =
        useState(false)

    const [confirmandoId, setConfirmandoId] =
        useState<string | null>(null)

    const [error, setError] =
        useState('')

    const [mostrarFormulario, setMostrarFormulario] =
        useState(false)

    const [compraSeleccionada, setCompraSeleccionada] =
        useState<Compra | null>(null)

    const [proveedorId, setProveedorId] =
        useState('')

    const [numeroDocumento, setNumeroDocumento] =
        useState('')

    const [fechaCompra, setFechaCompra] =
        useState(fechaLocalParaInput())

    const [observacion, setObservacion] =
        useState('')

    const [detalles, setDetalles] =
        useState<DetalleFormulario[]>([])

    const [contadorDetalle, setContadorDetalle] =
        useState(1)

    useEffect(() => {
        cargarDatos()
    }, [])

    async function cargarDatos() {
        try {
            setCargando(true)
            setError('')

            const [
                comprasRespuesta,
                proveedoresRespuesta,
                productosRespuesta
            ] = await Promise.all([
                obtenerCompras(),
                obtenerProveedores(),
                obtenerProductos()
            ])

            setCompras(comprasRespuesta)
            setProveedores(proveedoresRespuesta)
            setProductos(productosRespuesta)

        } catch {
            setError(
                'No fue posible cargar las compras.'
            )
        } finally {
            setCargando(false)
        }
    }

    function abrirFormulario() {
        setProveedorId('')
        setNumeroDocumento('')
        setFechaCompra(fechaLocalParaInput())
        setObservacion('')
        setDetalles([])
        setContadorDetalle(1)
        setError('')
        setMostrarFormulario(true)
    }

    function cerrarFormulario() {
        if (guardando) return

        setMostrarFormulario(false)
        setError('')
    }

    function agregarProducto() {
        setDetalles([
            ...detalles,
            {
                idTemporal: contadorDetalle,
                productoId: '',
                cantidad: '',
                precioUnitario: ''
            }
        ])

        setContadorDetalle(
            contadorDetalle + 1
        )
    }

    function actualizarDetalle(
        idTemporal: number,
        campo:
            | 'productoId'
            | 'cantidad'
            | 'precioUnitario',
        valor: string
    ) {
        setDetalles(
            detalles.map((detalle) => {
                if (
                    detalle.idTemporal !==
                    idTemporal
                ) {
                    return detalle
                }

                if (campo === 'productoId') {
                    return {
                        ...detalle,
                        productoId: valor
                    }
                }

                return {
                    ...detalle,
                    [campo]:
                        valor === ''
                            ? ''
                            : Number(valor)
                }
            })
        )
    }

    function eliminarDetalle(
        idTemporal: number
    ) {
        setDetalles(
            detalles.filter(
                (detalle) =>
                    detalle.idTemporal !==
                    idTemporal
            )
        )
    }

    const totalFormulario = useMemo(
        () =>
            detalles.reduce(
                (total, detalle) =>
                    total +
                    (Number(detalle.cantidad) || 0) *
                    (Number(detalle.precioUnitario) || 0),
                0
            ),
        [detalles]
    )

    async function guardarCompra(
        e: FormEvent<HTMLFormElement>
    ) {
        e.preventDefault()

        if (detalles.length === 0) {
            setError(
                'Debes agregar al menos un producto.'
            )
            return
        }

        if (
            detalles.some(
                (detalle) =>
                    !detalle.productoId ||
                    detalle.cantidad === '' ||
                    detalle.precioUnitario === '' ||
                    detalle.cantidad <= 0 ||
                    detalle.precioUnitario < 0
            )
        ) {
            setError(
                'Revisa los productos, cantidades y precios.'
            )
            return
        }

        try {
            setGuardando(true)
            setError('')

            const fechaInstant =
                new Date(fechaCompra)
                    .toISOString()

            await crearCompra({
                proveedorId,
                numeroDocumento,
                fechaCompra: fechaInstant,
                observacion,
                detalles: detalles.map(
                    ({
                         productoId,
                         cantidad,
                         precioUnitario
                     }) => ({
                        productoId,
                        cantidad: Number(cantidad),
                        precioUnitario:
                            Number(precioUnitario)
                    })
                )
            })

            await cargarDatos()

            setMostrarFormulario(false)

        } catch {
            setError(
                'No fue posible crear la compra.'
            )
        } finally {
            setGuardando(false)
        }
    }

    async function confirmar(
        compra: Compra
    ) {
        const aceptar = window.confirm(
            `¿Confirmar la compra ${
                compra.numeroDocumento ||
                'sin número de documento'
            }?\n\nAl confirmarla se ingresará el stock al inventario.`
        )

        if (!aceptar) return

        try {
            setConfirmandoId(compra.id)
            setError('')

            await confirmarCompra(compra.id)

            await cargarDatos()

            if (
                compraSeleccionada?.id ===
                compra.id
            ) {
                setCompraSeleccionada(null)
            }

        } catch {
            setError(
                'No fue posible confirmar la compra.'
            )
        } finally {
            setConfirmandoId(null)
        }
    }

    const comprasFiltradas = useMemo(() => {
        const texto =
            busqueda.trim().toLowerCase()

        if (!texto) return compras

        return compras.filter((compra) =>
            compra.proveedorRazonSocial
                .toLowerCase()
                .includes(texto) ||
            (compra.numeroDocumento || '')
                .toLowerCase()
                .includes(texto) ||
            compra.estado
                .toLowerCase()
                .includes(texto)
        )
    }, [compras, busqueda])

    const proveedoresActivos =
        proveedores.filter(
            (proveedor) =>
                proveedor.estado === 'ACTIVO'
        )

    const productosActivos =
        productos.filter(
            (producto) =>
                producto.estado === 'ACTIVO'
        )

    const formatoCLP =
        new Intl.NumberFormat(
            'es-CL',
            {
                style: 'currency',
                currency: 'CLP',
                maximumFractionDigits: 0
            }
        )

    const formatoFecha =
        new Intl.DateTimeFormat(
            'es-CL',
            {
                dateStyle: 'short',
                timeStyle: 'short'
            }
        )

    function claseEstado(
        estado: Compra['estado']
    ) {
        switch (estado) {
            case 'CONFIRMADA':
                return 'compras-estado compras-confirmada'

            case 'ANULADA':
                return 'compras-estado compras-anulada'

            default:
                return 'compras-estado compras-borrador'
        }
    }

    if (cargando) {
        return (
            <div className="compras-mensaje">
                Cargando compras...
            </div>
        )
    }

    return (
        <main className="compras-page">

            <header className="compras-header">

                <div>
                    <h1>Compras</h1>

                    <p>
                        Registra compras a proveedores
                        y entradas de inventario
                    </p>
                </div>

                <button
                    type="button"
                    className="compras-boton-principal"
                    onClick={abrirFormulario}
                >
                    + Nueva compra
                </button>

            </header>

            {error &&
                !mostrarFormulario && (
                    <div className="compras-error">
                        {error}
                    </div>
                )}

            <section className="compras-panel">

                <div className="compras-toolbar">

                    <input
                        type="search"
                        placeholder="Buscar por proveedor, documento o estado..."
                        value={busqueda}
                        onChange={(e) =>
                            setBusqueda(
                                e.target.value
                            )
                        }
                    />

                    <span>
                        {comprasFiltradas.length}
                        {' '}
                        compra
                        {comprasFiltradas.length !== 1
                            ? 's'
                            : ''}
                    </span>

                </div>

                <div className="compras-tabla-contenedor">

                    <table className="compras-tabla">

                        <thead>
                        <tr>
                            <th>Documento</th>
                            <th>Proveedor</th>
                            <th>Fecha</th>
                            <th>Total</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>

                        <tbody>

                        {comprasFiltradas.map(
                            (compra) => (

                                <tr key={compra.id}>

                                    <td>
                                        <strong>
                                            {
                                                compra.numeroDocumento ||
                                                'Sin documento'
                                            }
                                        </strong>
                                    </td>

                                    <td>
                                        {
                                            compra.proveedorRazonSocial
                                        }
                                    </td>

                                    <td>
                                        {
                                            formatoFecha.format(
                                                new Date(
                                                    compra.fechaCompra
                                                )
                                            )
                                        }
                                    </td>

                                    <td>
                                        <strong>
                                            {
                                                formatoCLP.format(
                                                    compra.total
                                                )
                                            }
                                        </strong>
                                    </td>

                                    <td>
                                            <span
                                                className={
                                                    claseEstado(
                                                        compra.estado
                                                    )
                                                }
                                            >
                                                {compra.estado}
                                            </span>
                                    </td>

                                    <td>
                                        <div className="compras-acciones">

                                            <button
                                                type="button"
                                                className="compras-boton-ver"
                                                onClick={() =>
                                                    setCompraSeleccionada(
                                                        compra
                                                    )
                                                }
                                            >
                                                Ver
                                            </button>

                                            {compra.estado ===
                                                'BORRADOR' && (
                                                    <button
                                                        type="button"
                                                        className="compras-boton-confirmar"
                                                        disabled={
                                                            confirmandoId ===
                                                            compra.id
                                                        }
                                                        onClick={() =>
                                                            confirmar(
                                                                compra
                                                            )
                                                        }
                                                    >
                                                        {confirmandoId ===
                                                        compra.id
                                                            ? 'Confirmando...'
                                                            : 'Confirmar'}
                                                    </button>
                                                )}

                                        </div>
                                    </td>

                                </tr>
                            )
                        )}

                        {comprasFiltradas.length ===
                            0 && (
                                <tr>
                                    <td
                                        colSpan={6}
                                        className="compras-vacio"
                                    >
                                        No hay compras para mostrar.
                                    </td>
                                </tr>
                            )}

                        </tbody>

                    </table>

                </div>

            </section>

            {mostrarFormulario && (

                <div className="compras-modal-fondo">

                    <div className="compras-modal compras-modal-grande">

                        <div className="compras-modal-header">

                            <div>
                                <h2>Nueva compra</h2>

                                <p>
                                    La compra se guardará
                                    inicialmente como BORRADOR
                                </p>
                            </div>

                            <button
                                type="button"
                                onClick={
                                    cerrarFormulario
                                }
                            >
                                ×
                            </button>

                        </div>

                        <form
                            className="compras-form"
                            onSubmit={guardarCompra}
                        >

                            <div className="compras-form-grid">

                                <label>
                                    Proveedor *

                                    <select
                                        required
                                        value={proveedorId}
                                        onChange={(e) =>
                                            setProveedorId(
                                                e.target.value
                                            )
                                        }
                                    >
                                        <option value="">
                                            Selecciona proveedor
                                        </option>

                                        {proveedoresActivos.map(
                                            (proveedor) => (
                                                <option
                                                    key={
                                                        proveedor.id
                                                    }
                                                    value={
                                                        proveedor.id
                                                    }
                                                >
                                                    {
                                                        proveedor.razonSocial
                                                    }
                                                </option>
                                            )
                                        )}

                                    </select>
                                </label>

                                <label>
                                    Número documento

                                    <input
                                        maxLength={50}
                                        value={
                                            numeroDocumento
                                        }
                                        onChange={(e) =>
                                            setNumeroDocumento(
                                                e.target.value
                                            )
                                        }
                                        placeholder="Ej: FACT-001"
                                    />
                                </label>

                                <label>
                                    Fecha compra *

                                    <input
                                        required
                                        type="datetime-local"
                                        value={fechaCompra}
                                        onChange={(e) =>
                                            setFechaCompra(
                                                e.target.value
                                            )
                                        }
                                    />
                                </label>

                            </div>

                            <label>
                                Observación

                                <textarea
                                    maxLength={500}
                                    value={observacion}
                                    onChange={(e) =>
                                        setObservacion(
                                            e.target.value
                                        )
                                    }
                                    placeholder="Información adicional..."
                                />
                            </label>

                            <div className="compras-detalles-header">

                                <div>
                                    <h3>Productos</h3>

                                    <p>
                                        Agrega los productos
                                        incluidos en la compra.
                                    </p>
                                </div>

                                <button
                                    type="button"
                                    className="compras-boton-agregar"
                                    onClick={
                                        agregarProducto
                                    }
                                >
                                    + Agregar producto
                                </button>

                            </div>

                            <div className="compras-detalles">

                                {detalles.length === 0 && (
                                    <div className="compras-vacio-detalle">
                                        Aún no has agregado productos.
                                    </div>
                                )}

                                {detalles.map(
                                    (detalle) => (

                                        <div
                                            className="compras-detalle-fila"
                                            key={
                                                detalle.idTemporal
                                            }
                                        >

                                            <label>
                                                Producto

                                                <select
                                                    required
                                                    value={
                                                        detalle.productoId
                                                    }
                                                    onChange={(e) =>
                                                        actualizarDetalle(
                                                            detalle.idTemporal,
                                                            'productoId',
                                                            e.target.value
                                                        )
                                                    }
                                                >
                                                    <option value="">
                                                        Seleccionar
                                                    </option>

                                                    {productosActivos.map(
                                                        (producto) => (
                                                            <option
                                                                key={
                                                                    producto.id
                                                                }
                                                                value={
                                                                    producto.id
                                                                }
                                                            >
                                                                {
                                                                    producto.sku
                                                                }
                                                                {' - '}
                                                                {
                                                                    producto.nombre
                                                                }
                                                            </option>
                                                        )
                                                    )}

                                                </select>
                                            </label>

                                            <label>
                                                Cantidad

                                                <input
                                                    required
                                                    type="number"
                                                    min="0.001"
                                                    step="0.001"
                                                    value={
                                                        detalle.cantidad
                                                    }
                                                    onChange={(e) =>
                                                        actualizarDetalle(
                                                            detalle.idTemporal,
                                                            'cantidad',
                                                            e.target.value
                                                        )
                                                    }
                                                />
                                            </label>

                                            <label>
                                                Precio unitario

                                                <input
                                                    required
                                                    type="number"
                                                    min="0"
                                                    step="1"
                                                    value={
                                                        detalle.precioUnitario
                                                    }
                                                    onChange={(e) =>
                                                        actualizarDetalle(
                                                            detalle.idTemporal,
                                                            'precioUnitario',
                                                            e.target.value
                                                        )
                                                    }
                                                />
                                            </label>

                                            <div className="compras-subtotal">

                                                <span>
                                                    Subtotal
                                                </span>

                                                <strong>
                                                    {
                                                        formatoCLP.format(
                                                            (Number(
                                                                detalle.cantidad
                                                            ) || 0) *
                                                            (Number(
                                                                detalle.precioUnitario
                                                            ) || 0)
                                                        )
                                                    }
                                                </strong>

                                            </div>

                                            <button
                                                type="button"
                                                className="compras-eliminar"
                                                title="Eliminar producto"
                                                onClick={() =>
                                                    eliminarDetalle(
                                                        detalle.idTemporal
                                                    )
                                                }
                                            >
                                                ×
                                            </button>

                                        </div>
                                    )
                                )}

                            </div>

                            <div className="compras-total">

                                <span>
                                    Total estimado
                                </span>

                                <strong>
                                    {
                                        formatoCLP.format(
                                            totalFormulario
                                        )
                                    }
                                </strong>

                            </div>

                            {error && (
                                <div className="compras-error">
                                    {error}
                                </div>
                            )}

                            <div className="compras-modal-acciones">

                                <button
                                    type="button"
                                    className="compras-boton-secundario"
                                    onClick={
                                        cerrarFormulario
                                    }
                                >
                                    Cancelar
                                </button>

                                <button
                                    type="submit"
                                    className="compras-boton-principal"
                                    disabled={guardando}
                                >
                                    {guardando
                                        ? 'Guardando...'
                                        : 'Crear compra'}
                                </button>

                            </div>

                        </form>

                    </div>

                </div>
            )}

            {compraSeleccionada && (

                <div className="compras-modal-fondo">

                    <div className="compras-modal compras-modal-grande">

                        <div className="compras-modal-header">

                            <div>
                                <h2>
                                    Compra{' '}
                                    {
                                        compraSeleccionada.numeroDocumento ||
                                        'sin documento'
                                    }
                                </h2>

                                <p>
                                    {
                                        compraSeleccionada.proveedorRazonSocial
                                    }
                                </p>
                            </div>

                            <button
                                type="button"
                                onClick={() =>
                                    setCompraSeleccionada(
                                        null
                                    )
                                }
                            >
                                ×
                            </button>

                        </div>

                        <div className="compras-modal-contenido">

                            <div className="compras-detalle-resumen">

                                <div>
                                    <span>Estado</span>

                                    <strong>
                                        {
                                            compraSeleccionada.estado
                                        }
                                    </strong>
                                </div>

                                <div>
                                    <span>Fecha</span>

                                    <strong>
                                        {
                                            formatoFecha.format(
                                                new Date(
                                                    compraSeleccionada.fechaCompra
                                                )
                                            )
                                        }
                                    </strong>
                                </div>

                                <div>
                                    <span>Total</span>

                                    <strong>
                                        {
                                            formatoCLP.format(
                                                compraSeleccionada.total
                                            )
                                        }
                                    </strong>
                                </div>

                            </div>

                            <table className="compras-tabla">

                                <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th>Cantidad</th>
                                    <th>Precio</th>
                                    <th>Subtotal</th>
                                </tr>
                                </thead>

                                <tbody>

                                {compraSeleccionada.detalles.map(
                                    (detalle) => (

                                        <tr key={detalle.id}>

                                            <td>
                                                {
                                                    detalle.productoNombre
                                                }
                                            </td>

                                            <td>
                                                {
                                                    detalle.cantidad
                                                }
                                            </td>

                                            <td>
                                                {
                                                    formatoCLP.format(
                                                        detalle.precioUnitario
                                                    )
                                                }
                                            </td>

                                            <td>
                                                <strong>
                                                    {
                                                        formatoCLP.format(
                                                            detalle.subtotal
                                                        )
                                                    }
                                                </strong>
                                            </td>

                                        </tr>
                                    )
                                )}

                                </tbody>

                            </table>

                            {compraSeleccionada.observacion && (
                                <div className="compras-observacion">

                                    <strong>
                                        Observación
                                    </strong>

                                    <p>
                                        {
                                            compraSeleccionada.observacion
                                        }
                                    </p>

                                </div>
                            )}

                            {compraSeleccionada.estado ===
                                'BORRADOR' && (

                                    <div className="compras-confirmacion">

                                        <p>
                                            Al confirmar esta compra,
                                            sus productos ingresarán
                                            automáticamente al inventario.
                                        </p>

                                        <button
                                            type="button"
                                            className="compras-boton-confirmar"
                                            disabled={
                                                confirmandoId ===
                                                compraSeleccionada.id
                                            }
                                            onClick={() =>
                                                confirmar(
                                                    compraSeleccionada
                                                )
                                            }
                                        >
                                            Confirmar compra
                                        </button>

                                    </div>
                                )}

                        </div>

                    </div>

                </div>
            )}

        </main>
    )
}

export default ComprasPage