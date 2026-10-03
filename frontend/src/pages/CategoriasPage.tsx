import {
    useEffect,
    useMemo,
    useState,
    type FormEvent
} from 'react'

import {
    activarCategoria,
    actualizarCategoria,
    crearCategoria,
    desactivarCategoria,
    obtenerCategorias,
    type Categoria,
    type CategoriaRequest
} from '../services/categoriaService'

import './CategoriasPage.css'
import { useNavigate } from 'react-router-dom'

const formularioInicial: CategoriaRequest = {
    nombre: '',
    descripcion: ''
}

function CategoriasPage() {
    const [categorias, setCategorias] = useState<Categoria[]>([])
    const [busqueda, setBusqueda] = useState('')
    const [cargando, setCargando] = useState(true)
    const [guardando, setGuardando] = useState(false)
    const [error, setError] = useState('')
    const [mostrarFormulario, setMostrarFormulario] = useState(false)
    const [categoriaEditando, setCategoriaEditando] =
        useState<Categoria | null>(null)
    const navigate = useNavigate()
    const [formulario, setFormulario] =
        useState<CategoriaRequest>(formularioInicial)

    useEffect(() => {
        cargarCategorias()
    }, [])

    async function cargarCategorias() {
        try {
            setError('')
            setCargando(true)
            setCategorias(await obtenerCategorias())
        } catch {
            setError('No fue posible cargar las categorías.')
        } finally {
            setCargando(false)
        }
    }

    function abrirNuevaCategoria() {
        setCategoriaEditando(null)
        setFormulario(formularioInicial)
        setError('')
        setMostrarFormulario(true)
    }

    function abrirEditarCategoria(categoria: Categoria) {
        setCategoriaEditando(categoria)

        setFormulario({
            nombre: categoria.nombre,
            descripcion: categoria.descripcion ?? ''
        })

        setError('')
        setMostrarFormulario(true)
    }

    function cerrarFormulario() {
        if (guardando) return

        setMostrarFormulario(false)
        setCategoriaEditando(null)
        setFormulario(formularioInicial)
        setError('')
    }

    async function guardarCategoria(e: FormEvent<HTMLFormElement>) {
        e.preventDefault()

        try {
            setError('')
            setGuardando(true)

            if (categoriaEditando) {
                await actualizarCategoria(
                    categoriaEditando.id,
                    formulario
                )
            } else {
                await crearCategoria(formulario)
            }

            await cargarCategorias()
            cerrarFormulario()
        } catch {
            setError('No fue posible guardar la categoría.')
        } finally {
            setGuardando(false)
        }
    }

    async function cambiarEstado(categoria: Categoria) {
        try {
            setError('')

            if (categoria.estado === 'ACTIVA') {
                await desactivarCategoria(categoria.id)
            } else {
                await activarCategoria(categoria.id)
            }

            await cargarCategorias()
        } catch {
            setError('No fue posible cambiar el estado de la categoría.')
        }
    }

    const categoriasFiltradas = useMemo(() => {
        const texto = busqueda.trim().toLowerCase()

        if (!texto) return categorias

        return categorias.filter((categoria) =>
            categoria.nombre.toLowerCase().includes(texto) ||
            categoria.descripcion?.toLowerCase().includes(texto)
        )
    }, [categorias, busqueda])

    if (cargando) {
        return <div className="categorias-mensaje">Cargando categorías...</div>
    }

    return (
        <main className="categorias-page">

            <header className="categorias-header">
                <div>
                    <button
                        className="categoria-volver"
                        type="button"
                        onClick={() => navigate('/productos')}
                    >
                        ← Volver a Productos
                    </button>

                    <h1>Categorías</h1>
                    <p>Organiza los productos de tu empresa</p>
                </div>

                <button
                    className="categoria-boton-principal"
                    type="button"
                    onClick={abrirNuevaCategoria}
                >
                    + Nueva categoría
                </button>
            </header>

            <section className="categorias-panel">

                <div className="categorias-toolbar">
                    <input
                        className="categorias-buscador"
                        type="search"
                        placeholder="Buscar categoría..."
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />

                    <span>
                        {categoriasFiltradas.length} categoría
                        {categoriasFiltradas.length !== 1 ? 's' : ''}
                    </span>
                </div>

                {error && !mostrarFormulario && (
                    <div className="categorias-error">{error}</div>
                )}

                <div className="categorias-tabla-contenedor">
                    <table className="categorias-tabla">
                        <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Descripción</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>

                        <tbody>
                        {categoriasFiltradas.map((categoria) => (
                            <tr key={categoria.id}>
                                <td>
                                    <strong>{categoria.nombre}</strong>
                                </td>

                                <td>
                                    {categoria.descripcion || '-'}
                                </td>

                                <td>
                                        <span
                                            className={
                                                categoria.estado === 'ACTIVA'
                                                    ? 'categoria-estado categoria-activa'
                                                    : 'categoria-estado categoria-inactiva'
                                            }
                                        >
                                            {categoria.estado}
                                        </span>
                                </td>

                                <td>
                                    <div className="categoria-acciones">
                                        <button
                                            className="categoria-editar"
                                            onClick={() =>
                                                abrirEditarCategoria(categoria)
                                            }
                                        >
                                            Editar
                                        </button>

                                        <button
                                            className={
                                                categoria.estado === 'ACTIVA'
                                                    ? 'categoria-estado-boton categoria-desactivar'
                                                    : 'categoria-estado-boton categoria-activar'
                                            }
                                            onClick={() =>
                                                cambiarEstado(categoria)
                                            }
                                        >
                                            {categoria.estado === 'ACTIVA'
                                                ? 'Desactivar'
                                                : 'Activar'}
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>

            </section>

            {mostrarFormulario && (
                <div className="categoria-modal-fondo">
                    <div className="categoria-modal">

                        <div className="categoria-modal-header">
                            <h2>
                                {categoriaEditando
                                    ? 'Editar categoría'
                                    : 'Nueva categoría'}
                            </h2>

                            <button onClick={cerrarFormulario}>
                                ×
                            </button>
                        </div>

                        <form onSubmit={guardarCategoria}>

                            <label>
                                Nombre *
                                <input
                                    required
                                    maxLength={100}
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
                                Descripción
                                <textarea
                                    maxLength={300}
                                    value={formulario.descripcion}
                                    onChange={(e) =>
                                        setFormulario({
                                            ...formulario,
                                            descripcion: e.target.value
                                        })
                                    }
                                />
                            </label>

                            {error && (
                                <div className="categorias-error">
                                    {error}
                                </div>
                            )}

                            <div className="categoria-modal-acciones">
                                <button
                                    type="button"
                                    className="categoria-cancelar"
                                    onClick={cerrarFormulario}
                                >
                                    Cancelar
                                </button>

                                <button
                                    className="categoria-boton-principal"
                                    disabled={guardando}
                                >
                                    {guardando
                                        ? 'Guardando...'
                                        : 'Guardar'}
                                </button>
                            </div>

                        </form>
                    </div>
                </div>
            )}

        </main>
    )
}

export default CategoriasPage