import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import './AppLayout.css'

function AppLayout() {
    const navigate = useNavigate()

    const nombre = localStorage.getItem('nombre') ?? 'Usuario'
    const rol = localStorage.getItem('rol') ?? ''

    function formatearRol(rol: string) {
        return rol
            .toLowerCase()
            .replaceAll('_', ' ')
            .replace(/\b\w/g, (letra) => letra.toUpperCase())
    }

    function cerrarSesion() {
        localStorage.removeItem('token')
        localStorage.removeItem('nombre')
        localStorage.removeItem('rol')

        navigate('/login')
    }

    return (
        <div className="app-layout">

            <aside className="sidebar">

                <div className="sidebar-brand">
                    <div className="brand-icon">
                        P
                    </div>

                    <div className="brand-text">
                        <h2>PymeFlow</h2>
                        <span>Gestión empresarial</span>
                    </div>
                </div>

                <nav className="sidebar-nav">

                    <NavLink to="/dashboard">
                        📊 Dashboard
                    </NavLink>

                    <NavLink to="/clientes">
                        👥 Clientes
                    </NavLink>

                    <NavLink to="/proveedores">
                        🏢 Proveedores
                    </NavLink>

                    <NavLink to="/productos">
                        📦 Productos
                    </NavLink>

                    <NavLink to="/inventario">
                        📋 Inventario
                    </NavLink>

                    <NavLink to="/compras">
                        🛒 Compras
                    </NavLink>

                    <NavLink to="/ventas">
                        💰 Ventas
                    </NavLink>

                </nav>

                <div className="sidebar-user">
                    <strong className="sidebar-user-name">
                        {nombre}
                    </strong>

                    <span className="sidebar-user-role">
                        {formatearRol(rol)}
                    </span>
                </div>

                <div className="sidebar-footer">
                    <button
                        type="button"
                        onClick={cerrarSesion}
                    >
                        Cerrar sesión
                    </button>
                </div>

            </aside>

            <section className="app-content">
                <Outlet />
            </section>

        </div>
    )
}

export default AppLayout