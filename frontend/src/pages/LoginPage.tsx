import { useState } from 'react'
import { login } from '../services/authService'
import { obtenerDashboard } from '../services/dashboardService'
interface LoginPageProps {
    onLoginExitoso: () => void
}
function LoginPage({ onLoginExitoso }: LoginPageProps) {

    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    async function handleLogin() {
        try {
            const respuesta = await login({
                email: email,
                password: password
            })

            localStorage.setItem('token', respuesta.token)
            const dashboard = await obtenerDashboard()

            console.log('Dashboard:', dashboard)
            onLoginExitoso()
            console.log(respuesta)
        } catch (error) {
            console.error('Error al iniciar sesión', error)
        }
    }

    return (
        <div>
            <h1>PymeFlow Chile</h1>
            <h2>Iniciar sesión</h2>

            <input
                type="email"
                placeholder="Correo electrónico"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />

            <input
                type="password"
                placeholder="Contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />

            <button onClick={handleLogin}>
                Ingresar
            </button>
        </div>
    )
}

export default LoginPage