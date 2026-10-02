import { useState } from 'react'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'

function App() {

    const [autenticado, setAutenticado] = useState(
        localStorage.getItem('token') !== null
    )

    if (autenticado) {
        return <DashboardPage />
    }

    return (
        <LoginPage
            onLoginExitoso={() => setAutenticado(true)}
        />
    )
}

export default App