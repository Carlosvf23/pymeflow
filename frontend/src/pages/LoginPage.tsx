import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../services/authService'

function LoginPage() {
    const navigate = useNavigate()

    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [cargando, setCargando] = useState(false)

    async function handleLogin(e: FormEvent<HTMLFormElement>) {
        e.preventDefault()

        try {
            setError('')
            setCargando(true)

            const respuesta = await login({
                email,
                password
            })

            localStorage.setItem('token', respuesta.token)
            localStorage.setItem('nombre', respuesta.nombre)
            localStorage.setItem('rol', respuesta.rol)

            navigate('/dashboard')

        } catch {
            setError('Correo o contraseña incorrectos.')
        } finally {
            setCargando(false)
        }
    }

    return (
        <main className="login-page">

            <div className="login-card">

                <div className="login-brand">
                    <div className="login-logo">P</div>

                    <h1>PymeFlow</h1>

                    <p>
                        Gestión empresarial simple y centralizada
                    </p>
                </div>

                <form
                    className="login-form"
                    onSubmit={handleLogin}
                >

                    <label>
                        Correo electrónico

                        <input
                            type="email"
                            value={email}
                            onChange={(e) =>
                                setEmail(e.target.value)
                            }
                            placeholder="correo@empresa.cl"
                            required
                        />
                    </label>

                    <label>
                        Contraseña

                        <input
                            type="password"
                            value={password}
                            onChange={(e) =>
                                setPassword(e.target.value)
                            }
                            placeholder="••••••••"
                            required
                        />
                    </label>

                    {error && (
                        <p className="login-error">
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={cargando}
                    >
                        {cargando
                            ? 'Ingresando...'
                            : 'Iniciar sesión'}
                    </button>

                </form>

            </div>

        </main>
    )
}

export default LoginPage