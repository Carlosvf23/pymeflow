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

            <Route
                path="/login"
                element={<LoginPage />}
            />

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
                    element={
                        <ModuloPage
                            titulo="Proveedores"
                            descripcion="Administra tus proveedores"
                        />
                    }
                />

                <Route
                    path="/productos"
                    element={
                        <ModuloPage
                            titulo="Productos"
                            descripcion="Gestiona tu catálogo de productos"
                        />
                    }
                />

                <Route
                    path="/inventario"
                    element={
                        <ModuloPage
                            titulo="Inventario"
                            descripcion="Consulta y controla el stock"
                        />
                    }
                />

                <Route
                    path="/compras"
                    element={
                        <ModuloPage
                            titulo="Compras"
                            descripcion="Gestiona las compras de tu empresa"
                        />
                    }
                />

                <Route
                    path="/ventas"
                    element={
                        <ModuloPage
                            titulo="Ventas"
                            descripcion="Gestiona las ventas de tu empresa"
                        />
                    }
                />

            </Route>

            <Route
                path="/"
                element={<Navigate to="/dashboard" replace />}
            />

            <Route
                path="*"
                element={<Navigate to="/dashboard" replace />}
            />

        </Routes>
    )
}

export default App