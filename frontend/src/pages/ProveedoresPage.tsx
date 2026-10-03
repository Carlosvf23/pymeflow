import {
    useEffect,
    useMemo,
    useState,
    type FormEvent
} from 'react'

import {
    activarProveedor,
    actualizarProveedor,
    crearProveedor,
    desactivarProveedor,
    obtenerProveedores,
    type Proveedor,
    type ProveedorRequest
} from '../services/proveedorService'

import './ProveedoresPage.css'

const formularioInicial: ProveedorRequest = {
    rut: '',
    razonSocial: '',
    contacto: '',
    email: '',
    telefono: '',
    direccion: '',
    comuna: '',
    region: '',
    sitioWeb: ''
}

function ProveedoresPage() {
    const [proveedores, setProveedores] =
        useState<Proveedor[]>([])

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

    const [proveedorEditando, setProveedorEditando] =
        useState<Proveedor | null>(null)

    const [formulario, setFormulario] =
        useState<ProveedorRequest>(formularioInicial)

    useEffect(() => {
        cargarProveedores()
    }, [])

    async function cargarProveedores() {
        try {
            setError('')
            setCargando(true)

            const datos = await obtenerProveedores()
            setProveedores(datos)

        } catch {
            setError(
                'No fue posible cargar los proveedores.'
            )
        } finally {
            setCargando(false)
        }
    }

    function abrirNuevoProveedor() {
        setProveedorEditando(null)
        setFormulario(formularioInicial)
        setError('')
        setMostrarFormulario(true)
    }

    function abrirEditarProveedor(
        proveedor: Proveedor
    ) {
        setProveedorEditando(proveedor)

        setFormulario({
            rut: proveedor.rut ?? '',
            razonSocial: proveedor.razonSocial,
            contacto: proveedor.contacto ?? '',
            email: proveedor.email ?? '',
            telefono: proveedor.telefono ?? '',
            direccion: proveedor.direccion ?? '',
            comuna: proveedor.comuna ?? '',
            region: proveedor.region ?? '',
            sitioWeb: proveedor.sitioWeb ?? ''
        })

        setError('')
        setMostrarFormulario(true)
    }

    function cerrarFormulario() {
        if (guardando) {
            return
        }

        setMostrarFormulario(false)
        setProveedorEditando(null)
        setFormulario(formularioInicial)
        setError('')
    }

    function actualizarCampo(
        campo: keyof ProveedorRequest,
        valor: string
    ) {
        setFormulario((actual) => ({
            ...actual,
            [campo]: valor
        }))
    }

    async function guardarProveedor(
        e: FormEvent<HTMLFormElement>
    ) {
        e.preventDefault()

        try {
            setError('')
            setGuardando(true)

            if (proveedorEditando) {
                await actualizarProveedor(
                    proveedorEditando.id,
                    formulario
                )
            } else {
                await crearProveedor(formulario)
            }

            await cargarProveedores()

            setMostrarFormulario(false)
            setProveedorEditando(null)
            setFormulario(formularioInicial)

        } catch {
            setError(
                'No fue posible guardar el proveedor. Revisa los datos ingresados.'
            )
        } finally {
            setGuardando(false)
        }
    }

    async function cambiarEstado(
        proveedor: Proveedor
    ) {
        try {
            setError('')

            if (proveedor.estado === 'ACTIVO') {
                await desactivarProveedor(proveedor.id)
            } else {
                await activarProveedor(proveedor.id)
            }

            await cargarProveedores()

        } catch {
            setError(
                `No fue posible ${
                    proveedor.estado === 'ACTIVO'
                        ? 'desactivar'
                        : 'activar'
                } el proveedor.`
            )
        }
    }

    const proveedoresFiltrados = useMemo(() => {
        const texto =
            busqueda.trim().toLowerCase()

        if (!texto) {
            return proveedores
        }

        return proveedores.filter((proveedor) =>
            proveedor.razonSocial
                .toLowerCase()
                .includes(texto) ||

            proveedor.rut
                ?.toLowerCase()
                .includes(texto) ||

            proveedor.contacto
                ?.toLowerCase()
                .includes(texto) ||

            proveedor.email
                ?.toLowerCase()
                .includes(texto)
        )
    }, [proveedores, busqueda])

    if (cargando) {
        return (
            <div className="proveedores-mensaje">
                Cargando proveedores...
            </div>
        )
    }

    return (
        <main className="proveedores-page">

            <header className="proveedores-header">
                <div>
                    <h1>Proveedores</h1>
                    <p>
                        Administra los proveedores de tu empresa
                    </p>
                </div>

                <button
                    className="proveedor-boton-principal"
                    type="button"
                    onClick={abrirNuevoProveedor}
                >
                    + Nuevo proveedor
                </button>
            </header>

            <section className="proveedores-panel">

                <div className="proveedores-toolbar">

                    <input
                        className="proveedores-buscador"
                        type="search"
                        placeholder="Buscar por nombre, RUT, contacto o email..."
                        value={busqueda}
                        onChange={(e) =>
                            setBusqueda(e.target.value)
                        }
                    />

                    <span className="proveedores-total">
                        {proveedoresFiltrados.length}
                        {' '}
                        proveedor
                        {proveedoresFiltrados.length !== 1
                            ? 'es'
                            : ''}
                    </span>

                </div>

                {error && !mostrarFormulario && (
                    <div className="proveedores-error">
                        {error}
                    </div>
                )}

                {proveedoresFiltrados.length === 0 ? (

                    <div className="proveedores-vacio">
                        <h3>No hay proveedores</h3>
                        <p>
                            Crea tu primer proveedor para comenzar.
                        </p>
                    </div>

                ) : (

                    <div className="proveedores-tabla-contenedor">

                        <table className="proveedores-tabla">

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

                            {proveedoresFiltrados.map(
                                (proveedor) => (

                                    <tr key={proveedor.id}>

                                        <td>
                                            {proveedor.rut || '-'}
                                        </td>

                                        <td>
                                            <div className="proveedor-empresa">
                                                <strong>
                                                    {proveedor.razonSocial}
                                                </strong>

                                                {proveedor.sitioWeb && (
                                                    <small>
                                                        {proveedor.sitioWeb}
                                                    </small>
                                                )}
                                            </div>
                                        </td>

                                        <td>
                                            <div className="proveedor-contacto">
                                                    <span>
                                                        {proveedor.contacto || '-'}
                                                    </span>

                                                <small>
                                                    {proveedor.email || ''}
                                                </small>

                                                <small>
                                                    {proveedor.telefono || ''}
                                                </small>
                                            </div>
                                        </td>

                                        <td>
                                            {proveedor.comuna
                                                ? `${proveedor.comuna}${proveedor.region
                                                    ? `, ${proveedor.region}`
                                                    : ''}`
                                                : '-'}
                                        </td>

                                        <td>
                                                <span
                                                    className={
                                                        proveedor.estado === 'ACTIVO'
                                                            ? 'proveedor-estado proveedor-estado-activo'
                                                            : 'proveedor-estado proveedor-estado-inactivo'
                                                    }
                                                >
                                                    {proveedor.estado}
                                                </span>
                                        </td>

                                        <td>
                                            <div className="proveedor-acciones">

                                                <button
                                                    className="proveedor-boton-editar"
                                                    type="button"
                                                    onClick={() =>
                                                        abrirEditarProveedor(proveedor)
                                                    }
                                                >
                                                    Editar
                                                </button>

                                                <button
                                                    className={
                                                        proveedor.estado === 'ACTIVO'
                                                            ? 'proveedor-boton-estado proveedor-boton-desactivar'
                                                            : 'proveedor-boton-estado proveedor-boton-activar'
                                                    }
                                                    type="button"
                                                    onClick={() =>
                                                        cambiarEstado(proveedor)
                                                    }
                                                >
                                                    {proveedor.estado === 'ACTIVO'
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

                <div className="proveedor-modal-fondo">

                    <div className="proveedor-modal">

                        <div className="proveedor-modal-header">

                            <div>
                                <h2>
                                    {proveedorEditando
                                        ? 'Editar proveedor'
                                        : 'Nuevo proveedor'}
                                </h2>

                                <p>
                                    Completa la información del proveedor
                                </p>
                            </div>

                            <button
                                className="proveedor-modal-cerrar"
                                type="button"
                                onClick={cerrarFormulario}
                            >
                                ×
                            </button>

                        </div>

                        <form
                            className="proveedor-form"
                            onSubmit={guardarProveedor}
                        >

                            <div className="proveedor-form-grid">

                                <label>
                                    Razón social *

                                    <input
                                        required
                                        maxLength={150}
                                        value={formulario.razonSocial}
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
                                    Contacto

                                    <input
                                        maxLength={150}
                                        value={formulario.contacto}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'contacto',
                                                e.target.value
                                            )
                                        }
                                        placeholder="Nombre del contacto"
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
                                        placeholder="contacto@proveedor.cl"
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

                                <label>
                                    Sitio web

                                    <input
                                        maxLength={200}
                                        value={formulario.sitioWeb}
                                        onChange={(e) =>
                                            actualizarCampo(
                                                'sitioWeb',
                                                e.target.value
                                            )
                                        }
                                        placeholder="https://..."
                                    />
                                </label>

                                <label className="proveedor-form-ancho">
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
                                <div className="proveedores-error">
                                    {error}
                                </div>
                            )}

                            <div className="proveedor-modal-acciones">

                                <button
                                    type="button"
                                    className="proveedor-boton-secundario"
                                    onClick={cerrarFormulario}
                                    disabled={guardando}
                                >
                                    Cancelar
                                </button>

                                <button
                                    type="submit"
                                    className="proveedor-boton-principal"
                                    disabled={guardando}
                                >
                                    {guardando
                                        ? 'Guardando...'
                                        : proveedorEditando
                                            ? 'Guardar cambios'
                                            : 'Crear proveedor'}
                                </button>

                            </div>

                        </form>

                    </div>

                </div>
            )}

        </main>
    )
}

export default ProveedoresPage