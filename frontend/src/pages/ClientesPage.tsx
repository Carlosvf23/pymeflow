import {
    useEffect,
    useMemo,
    useState,
    type FormEvent
} from 'react'

import {
    activarCliente,
    actualizarCliente,
    crearCliente,
    desactivarCliente,
    obtenerClientes,
    type Cliente,
    type ClienteRequest
} from '../services/clienteService'

import {
    formatearRut,
    validarRut
} from '../utils/rut'

import './ClientesPage.css'

const formularioInicial: ClienteRequest = {
    rut: '',
    razonSocial: '',
    email: '',
    telefono: '',
    direccion: '',
    comuna: '',
    region: ''
}

function ClientesPage() {
    const [clientes, setClientes] =
        useState<Cliente[]>([])

    const [busqueda, setBusqueda] =
        useState('')

    const [cargando, setCargando] =
        useState(true)

    const [guardando, setGuardando] =
        useState(false)

    const [error, setError] =
        useState('')

    const [mostrarFormulario, setMostrarFormulario] =
        useState(false)

    const [clienteEditando, setClienteEditando] =
        useState<Cliente | null>(null)

    const [formulario, setFormulario] =
        useState<ClienteRequest>(formularioInicial)

    useEffect(() => {
        cargarClientes()
    }, [])

    async function cargarClientes() {
        try {
            setError('')
            setCargando(true)

            const datos = await obtenerClientes()

            setClientes(datos)

        } catch {
            setError(
                'No fue posible cargar los clientes.'
            )
        } finally {
            setCargando(false)
        }
    }

    function abrirNuevoCliente() {
        setClienteEditando(null)
        setFormulario(formularioInicial)
        setError('')
        setMostrarFormulario(true)
    }

    function abrirEditarCliente(cliente: Cliente) {
        setClienteEditando(cliente)

        setFormulario({
            rut: cliente.rut
                ? formatearRut(cliente.rut)
                : '',
            razonSocial: cliente.razonSocial,
            email: cliente.email ?? '',
            telefono: cliente.telefono ?? '',
            direccion: cliente.direccion ?? '',
            comuna: cliente.comuna ?? '',
            region: cliente.region ?? ''
        })

        setError('')
        setMostrarFormulario(true)
    }

    function cerrarFormulario() {
        if (guardando) {
            return
        }

        setMostrarFormulario(false)
        setClienteEditando(null)
        setFormulario(formularioInicial)
        setError('')
    }

    function actualizarCampo(
        campo: keyof ClienteRequest,
        valor: string
    ) {
        setFormulario((actual) => ({
            ...actual,
            [campo]:
                campo === 'rut'
                    ? formatearRut(valor)
                    : valor
        }))
    }

    async function guardarCliente(
        e: FormEvent<HTMLFormElement>
    ) {
        e.preventDefault()

        if (
            formulario.rut &&
            !validarRut(formulario.rut)
        ) {
            setError(
                'El RUT ingresado no es válido.'
            )
            return
        }

        try {
            setError('')
            setGuardando(true)

            if (clienteEditando) {
                await actualizarCliente(
                    clienteEditando.id,
                    formulario
                )
            } else {
                await crearCliente(formulario)
            }

            await cargarClientes()

            setMostrarFormulario(false)
            setClienteEditando(null)
            setFormulario(formularioInicial)

        } catch {
            setError(
                'No fue posible guardar el cliente. Revisa los datos ingresados.'
            )
        } finally {
            setGuardando(false)
        }
    }

    async function cambiarEstado(cliente: Cliente) {
        try {
            setError('')

            if (cliente.estado === 'ACTIVO') {
                await desactivarCliente(cliente.id)
            } else {
                await activarCliente(cliente.id)
            }

            await cargarClientes()

        } catch {
            setError(
                `No fue posible ${
                    cliente.estado === 'ACTIVO'
                        ? 'desactivar'
                        : 'activar'
                } el cliente.`
            )
        }
    }

    const clientesFiltrados = useMemo(() => {
        const texto =
            busqueda.trim().toLowerCase()

        if (!texto) {
            return clientes
        }

        return clientes.filter((cliente) =>
            cliente.razonSocial
                .toLowerCase()
                .includes(texto) ||

            cliente.rut
                ?.toLowerCase()
                .includes(texto) ||

            cliente.email
                ?.toLowerCase()
                .includes(texto)
        )
    }, [clientes, busqueda])

    if (cargando) {
        return (
            <div className="clientes-mensaje">
                Cargando clientes...
            </div>
        )
    }

    return (
        <main className="clientes-page">

            <header className="clientes-header">

                <div>
                    <h1>Clientes</h1>

                    <p>
                        Administra los clientes de tu empresa
                    </p>
                </div>

                <button
                    className="boton-principal"
                    type="button"
                    onClick={abrirNuevoCliente}
                >
                    + Nuevo cliente
                </button>

            </header>

            <section className="clientes-panel">

                <div className="clientes-toolbar">

                    <input
                        className="clientes-buscador"
                        type="search"
                        placeholder="Buscar por nombre, RUT o email..."
                        value={busqueda}
                        onChange={(e) =>
                            setBusqueda(e.target.value)
                        }
                    />

                    <span className="clientes-total">
                        {clientesFiltrados.length}
                        {' '}
                        cliente
                        {clientesFiltrados.length !== 1
                            ? 's'
                            : ''}
                    </span>

                </div>

                {error && !mostrarFormulario && (
                    <div className="clientes-error">
                        {error}
                    </div>
                )}

                {clientesFiltrados.length === 0 ? (

                    <div className="clientes-vacio">
                        <h3>No hay clientes</h3>

                        <p>
                            Crea tu primer cliente para comenzar.
                        </p>
                    </div>

                ) : (

                    <div className="clientes-tabla-contenedor">

                        <table className="clientes-tabla">

                            <thead>
                            <tr>
                                <th>RUT</th>
                                <th>Razón social</th>
                                <th>Contacto</th>
                                <th>Ubicación</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                            </thead>

                            <tbody>

                            {clientesFiltrados.map(
                                (cliente) => (

                                    <tr key={cliente.id}>

                                        <td>
                                            {cliente.rut
                                                ? formatearRut(cliente.rut)
                                                : '-'}
                                        </td>

                                        <td>
                                            <strong>
                                                {cliente.razonSocial}
                                            </strong>
                                        </td>

                                        <td>
                                            <div className="cliente-contacto">
                                                    <span>
                                                        {cliente.email || '-'}
                                                    </span>

                                                <small>
                                                    {cliente.telefono || ''}
                                                </small>
                                            </div>
                                        </td>

                                        <td>
                                            {cliente.comuna
                                                ? `${cliente.comuna}${cliente.region
                                                    ? `, ${cliente.region}`
                                                    : ''}`
                                                : '-'}
                                        </td>

                                        <td>
                                                <span
                                                    className={
                                                        cliente.estado === 'ACTIVO'
                                                            ? 'estado estado-activo'
                                                            : 'estado estado-inactivo'
                                                    }
                                                >
                                                    {cliente.estado}
                                                </span>
                                        </td>

                                        <td>
                                            <div className="cliente-acciones">

                                                <button
                                                    className="boton-editar"
                                                    type="button"
                                                    onClick={() =>
                                                        abrirEditarCliente(cliente)
                                                    }
                                                >
                                                    Editar
                                                </button>

                                                <button
                                                    className={
                                                        cliente.estado === 'ACTIVO'
                                                            ? 'boton-estado boton-desactivar'
                                                            : 'boton-estado boton-activar'
                                                    }
                                                    type="button"
                                                    onClick={() =>
                                                        cambiarEstado(cliente)
                                                    }
                                                >
                                                    {cliente.estado === 'ACTIVO'
                                                        ? 'Desactivar'
                                                        : 'Activar'}
                                                </button>

                                            </div>
                                        </td>

                                    </tr>
                                )
                            )}

                            </tbody>

                        </table>

                    </div>
                )}

            </section>

            {mostrarFormulario && (

                <div className="modal-fondo">

                    <div className="cliente-modal">

                        <div className="modal-header">

                            <div>
                                <h2>
                                    {clienteEditando
                                        ? 'Editar cliente'
                                        : 'Nuevo cliente'}
                                </h2>

                                <p>
                                    Completa la información del cliente
                                </p>
                            </div>

                            <button
                                className="modal-cerrar"
                                type="button"
                                onClick={cerrarFormulario}
                            >
                                ×
                            </button>

                        </div>

                        <form
                            className="cliente-form"
                            onSubmit={guardarCliente}
                        >

                            <div className="form-grid">

                                <label>
                                    Razón social *

                                    <input
                                        required
                                        maxLength={150}
                                        value={
                                            formulario.razonSocial
                                        }
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'razonSocial',
                                                e.target.value
                                            )
                                        }
                                    />
                                </label>

                                <label>
                                    RUT

                                    <input
                                        maxLength={12}
                                        value={formulario.rut}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'rut',
                                                e.target.value
                                            )
                                        }
                                        placeholder="12.345.678-9"
                                    />
                                </label>

                                <label>
                                    Email

                                    <input
                                        type="email"
                                        maxLength={150}
                                        value={formulario.email}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'email',
                                                e.target.value
                                            )
                                        }
                                        placeholder="cliente@correo.cl"
                                    />
                                </label>

                                <label>
                                    Teléfono

                                    <input
                                        maxLength={30}
                                        value={formulario.telefono}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'telefono',
                                                e.target.value
                                            )
                                        }
                                        placeholder="+56 9..."
                                    />
                                </label>

                                <label className="form-ancho-completo">
                                    Dirección

                                    <input
                                        maxLength={200}
                                        value={formulario.direccion}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'direccion',
                                                e.target.value
                                            )
                                        }
                                    />
                                </label>

                                <label>
                                    Comuna

                                    <input
                                        maxLength={100}
                                        value={formulario.comuna}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'comuna',
                                                e.target.value
                                            )
                                        }
                                    />
                                </label>

                                <label>
                                    Región

                                    <input
                                        maxLength={100}
                                        value={formulario.region}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'region',
                                                e.target.value
                                            )
                                        }
                                    />
                                </label>

                            </div>

                            {error && (
                                <div className="clientes-error">
                                    {error}
                                </div>
                            )}

                            <div className="modal-acciones">

                                <button
                                    type="button"
                                    className="boton-secundario"
                                    onClick={cerrarFormulario}
                                    disabled={guardando}
                                >
                                    Cancelar
                                </button>

                                <button
                                    type="submit"
                                    className="boton-principal"
                                    disabled={guardando}
                                >
                                    {guardando
                                        ? 'Guardando...'
                                        : clienteEditando
                                            ? 'Guardar cambios'
                                            : 'Crear cliente'}
                                </button>

                            </div>

                        </form>

                    </div>

                </div>
            )}

        </main>
    )
}

export default ClientesPage