import {
    useEffect,
    useMemo,
    useState,
    type FormEvent
} from 'react'

import {
    obtenerVentas,
    crearVenta,
    confirmarVenta,
    type Venta
} from '../services/ventaService'

import {
    obtenerClientes,
    type Cliente
} from '../services/clienteService'

import {
    obtenerProductos,
    type Producto
} from '../services/productoService'

import './VentasPage.css'

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

function VentasPage() {
    const [ventas, setVentas] =
        useState<Venta[]>([])

    const [clientes, setClientes] =
        useState<Cliente[]>([])

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

    const [ventaSeleccionada, setVentaSeleccionada] =
        useState<Venta | null>(null)

    const [clienteId, setClienteId] =
        useState('')

    const [numeroDocumento, setNumeroDocumento] =
        useState('')

    const [fechaVenta, setFechaVenta] =
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
                ventasRespuesta,
                clientesRespuesta,
                productosRespuesta
            ] = await Promise.all([
                obtenerVentas(),
                obtenerClientes(),
                obtenerProductos()
            ])

            setVentas(ventasRespuesta)
            setClientes(clientesRespuesta)
            setProductos(productosRespuesta)

        } catch {
            setError(
                'No fue posible cargar las ventas.'
            )
        } finally {
            setCargando(false)
        }
    }

    function abrirFormulario() {
        setClienteId('')
        setNumeroDocumento('')
        setFechaVenta(fechaLocalParaInput())
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

    async function guardarVenta(
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
                new Date(fechaVenta)
                    .toISOString()

            await crearVenta({
                clienteId,
                numeroDocumento,
                fechaVenta: fechaInstant,
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
                'No fue posible crear la venta.'
            )
        } finally {
            setGuardando(false)
        }
    }

    async function confirmar(
        venta: Venta
    ) {
        const aceptar = window.confirm(
            `¿Confirmar la venta ${
                venta.numeroDocumento ||
                'sin número de documento'
            }?\n\nAl confirmarla se descontará el stock del inventario.`
        )

        if (!aceptar) return

        try {
            setConfirmandoId(venta.id)
            setError('')

            await confirmarVenta(venta.id)

            await cargarDatos()

            if (
                ventaSeleccionada?.id ===
                venta.id
            ) {
                setVentaSeleccionada(null)
            }

        } catch {
            setError(
                'No fue posible confirmar la venta. Revisa que exista stock suficiente para todos los productos.'
            )
        } finally {
            setConfirmandoId(null)
        }
    }

    const ventasFiltradas = useMemo(() => {
        const texto =
            busqueda.trim().toLowerCase()

        if (!texto) return ventas

        return ventas.filter((venta) =>
            venta.clienteRazonSocial
                .toLowerCase()
                .includes(texto) ||
            (venta.numeroDocumento || '')
                .toLowerCase()
                .includes(texto) ||
            venta.estado
                .toLowerCase()
                .includes(texto)
        )
    }, [ventas, busqueda])

    const clientesActivos =
        clientes.filter(
            (cliente) =>
                cliente.estado === 'ACTIVO'
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
        estado: Venta['estado']
    ) {
        switch (estado) {
            case 'CONFIRMADA':
                return 'ventas-estado ventas-confirmada'

            case 'ANULADA':
                return 'ventas-estado ventas-anulada'

            default:
                return 'ventas-estado ventas-borrador'
        }
    }

    if (cargando) {
        return (
            <div className="ventas-mensaje">
                Cargando ventas...
            </div>
        )
    }

    return (
        <main className="ventas-page">

            <header className="ventas-header">

                <div>
                    <h1>Ventas</h1>

                    <p>
                        Registra ventas a clientes
                        y salidas de inventario
                    </p>
                </div>

                <button
                    type="button"
                    className="ventas-boton-principal"
                    onClick={abrirFormulario}
                >
                    + Nueva venta
                </button>

            </header>

            {error &&
                !mostrarFormulario && (
                    <div className="ventas-error">
                        {error}
                    </div>
                )}

            <section className="ventas-panel">

                <div className="ventas-toolbar">

                    <input
                        type="search"
                        placeholder="Buscar por cliente, documento o estado..."
                        value={busqueda}
                        onChange={(e) =>
                            setBusqueda(
                                e.target.value
                            )
                        }
                    />

                    <span>
                        {ventasFiltradas.length}
                        {' '}
                        venta
                        {ventasFiltradas.length !== 1
                            ? 's'
                            : ''}
                    </span>

                </div>

                <div className="ventas-tabla-contenedor">

                    <table className="ventas-tabla">

                        <thead>
                        <tr>
                            <th>Documento</th>
                            <th>Cliente</th>
                            <th>Fecha</th>
                            <th>Total</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>

                        <tbody>

                        {ventasFiltradas.map(
                            (venta) => (

                                <tr key={venta.id}>

                                    <td>
                                        <strong>
                                            {
                                                venta.numeroDocumento ||
                                                'Sin documento'
                                            }
                                        </strong>
                                    </td>

                                    <td>
                                        {
                                            venta.clienteRazonSocial
                                        }
                                    </td>

                                    <td>
                                        {
                                            formatoFecha.format(
                                                new Date(
                                                    venta.fechaVenta
                                                )
                                            )
                                        }
                                    </td>

                                    <td>
                                        <strong>
                                            {
                                                formatoCLP.format(
                                                    venta.total
                                                )
                                            }
                                        </strong>
                                    </td>

                                    <td>
                                            <span
                                                className={
                                                    claseEstado(
                                                        venta.estado
                                                    )
                                                }
                                            >
                                                {venta.estado}
                                            </span>
                                    </td>

                                    <td>
                                        <div className="ventas-acciones">

                                            <button
                                                type="button"
                                                className="ventas-boton-ver"
                                                onClick={() =>
                                                    setVentaSeleccionada(
                                                        venta
                                                    )
                                                }
                                            >
                                                Ver
                                            </button>

                                            {venta.estado ===
                                                'BORRADOR' && (
                                                    <button
                                                        type="button"
                                                        className="ventas-boton-confirmar"
                                                        disabled={
                                                            confirmandoId ===
                                                            venta.id
                                                        }
                                                        onClick={() =>
                                                            confirmar(
                                                                venta
                                                            )
                                                        }
                                                    >
                                                        {confirmandoId ===
                                                        venta.id
                                                            ? 'Confirmando...'
                                                            : 'Confirmar'}
                                                    </button>
                                                )}

                                        </div>
                                    </td>

                                </tr>
                            )
                        )}

                        {ventasFiltradas.length ===
                            0 && (
                                <tr>
                                    <td
                                        colSpan={6}
                                        className="ventas-vacio"
                                    >
                                        No hay ventas para mostrar.
                                    </td>
                                </tr>
                            )}

                        </tbody>

                    </table>

                </div>

            </section>

            {mostrarFormulario && (

                <div className="ventas-modal-fondo">

                    <div className="ventas-modal ventas-modal-grande">

                        <div className="ventas-modal-header">

                            <div>
                                <h2>Nueva venta</h2>

                                <p>
                                    La venta se guardará
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
                            className="ventas-form"
                            onSubmit={guardarVenta}
                        >

                            <div className="ventas-form-grid">

                                <label>
                                    Cliente *

                                    <select
                                        required
                                        value={clienteId}
                                        onChange={(e) =>
                                            setClienteId(
                                                e.target.value
                                            )
                                        }
                                    >
                                        <option value="">
                                            Selecciona cliente
                                        </option>

                                        {clientesActivos.map(
                                            (cliente) => (
                                                <option
                                                    key={
                                                        cliente.id
                                                    }
                                                    value={
                                                        cliente.id
                                                    }
                                                >
                                                    {
                                                        cliente.razonSocial
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
                                        placeholder="Ej: VENTA-001"
                                    />
                                </label>

                                <label>
                                    Fecha venta *

                                    <input
                                        required
                                        type="datetime-local"
                                        value={fechaVenta}
                                        onChange={(e) =>
                                            setFechaVenta(
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

                            <div className="ventas-detalles-header">

                                <div>
                                    <h3>Productos</h3>

                                    <p>
                                        Agrega los productos
                                        incluidos en la venta.
                                    </p>
                                </div>

                                <button
                                    type="button"
                                    className="ventas-boton-agregar"
                                    onClick={
                                        agregarProducto
                                    }
                                >
                                    + Agregar producto
                                </button>

                            </div>

                            <div className="ventas-detalles">

                                {detalles.length === 0 && (
                                    <div className="ventas-vacio-detalle">
                                        Aún no has agregado productos.
                                    </div>
                                )}

                                {detalles.map(
                                    (detalle) => (

                                        <div
                                            className="ventas-detalle-fila"
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

                                            <div className="ventas-subtotal">

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
                                                className="ventas-eliminar"
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

                            <div className="ventas-total">

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
                                <div className="ventas-error">
                                    {error}
                                </div>
                            )}

                            <div className="ventas-modal-acciones">

                                <button
                                    type="button"
                                    className="ventas-boton-secundario"
                                    onClick={
                                        cerrarFormulario
                                    }
                                >
                                    Cancelar
                                </button>

                                <button
                                    type="submit"
                                    className="ventas-boton-principal"
                                    disabled={guardando}
                                >
                                    {guardando
                                        ? 'Guardando...'
                                        : 'Crear venta'}
                                </button>

                            </div>

                        </form>

                    </div>

                </div>
            )}

            {ventaSeleccionada && (

                <div className="ventas-modal-fondo">

                    <div className="ventas-modal ventas-modal-grande">

                        <div className="ventas-modal-header">

                            <div>
                                <h2>
                                    Venta{' '}
                                    {
                                        ventaSeleccionada.numeroDocumento ||
                                        'sin documento'
                                    }
                                </h2>

                                <p>
                                    {
                                        ventaSeleccionada.clienteRazonSocial
                                    }
                                </p>
                            </div>

                            <button
                                type="button"
                                onClick={() =>
                                    setVentaSeleccionada(
                                        null
                                    )
                                }
                            >
                                ×
                            </button>

                        </div>

                        <div className="ventas-modal-contenido">

                            <div className="ventas-detalle-resumen">

                                <div>
                                    <span>Estado</span>

                                    <strong>
                                        {
                                            ventaSeleccionada.estado
                                        }
                                    </strong>
                                </div>

                                <div>
                                    <span>Fecha</span>

                                    <strong>
                                        {
                                            formatoFecha.format(
                                                new Date(
                                                    ventaSeleccionada.fechaVenta
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
                                                ventaSeleccionada.total
                                            )
                                        }
                                    </strong>
                                </div>

                            </div>

                            <table className="ventas-tabla">

                                <thead>
                                <tr>
                                    <th>Producto</th>
                                    <th>Cantidad</th>
                                    <th>Precio</th>
                                    <th>Subtotal</th>
                                </tr>
                                </thead>

                                <tbody>

                                {ventaSeleccionada.detalles.map(
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

                            {ventaSeleccionada.observacion && (
                                <div className="ventas-observacion">

                                    <strong>
                                        Observación
                                    </strong>

                                    <p>
                                        {
                                            ventaSeleccionada.observacion
                                        }
                                    </p>

                                </div>
                            )}

                            {ventaSeleccionada.estado ===
                                'BORRADOR' && (

                                    <div className="ventas-confirmacion">

                                        <p>
                                            Al confirmar esta venta,
                                            sus productos se descontarán
                                            automáticamente del inventario.
                                        </p>

                                        <button
                                            type="button"
                                            className="ventas-boton-confirmar"
                                            disabled={
                                                confirmandoId ===
                                                ventaSeleccionada.id
                                            }
                                            onClick={() =>
                                                confirmar(
                                                    ventaSeleccionada
                                                )
                                            }
                                        >
                                            Confirmar venta
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

export default VentasPage