import {
    Navigate,
    Route,
    Routes
} from 'react-router-dom'

import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import ModuloPage from './pages/ModuloPage'
import AppLayout from './components/AppLayout'
import ClientesPage from './pages/ClientesPage'
import ProveedoresPage from './pages/ProveedoresPage'
import CategoriasPage from './pages/CategoriasPage'
import ProductosPage from './pages/ProductosPage'

function RutaProtegida() {
    const token = localStorage.getItem('token')

    if (!token) {
        return <Navigate to="/login" replace />
    }

    return <AppLayout />
}

function App() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route element={<RutaProtegida />}>

                <Route
                    path="/dashboard"
                    element={<DashboardPage />}
                />

                <Route
                    path="/clientes"
                    element={<ClientesPage />}
                />

                <Route
                    path="/proveedores"
                    element={<ProveedoresPage />}
                />

                <Route
                    path="/productos"
                    element={<ProductosPage />}
                />

                {/* AQUÍ */}
                <Route
                    path="/categorias"
                    element={<CategoriasPage />}
                />

                <Route
                    path="/inventario"
                    element={
                        <ModuloPage
                            titulo="Inventario"
                            descripcion="Control de stock y movimientos"
                        />
                    }
                />

                {/* después siguen Compras, Ventas, etc. */}

            </Route>
        </Routes>
    )
}

export default App