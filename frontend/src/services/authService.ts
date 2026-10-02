import api from '../api/axios'

export interface LoginRequest {
    email: string
    password: string
}

export async function login(datos: LoginRequest) {
    const respuesta = await api.post('/auth/login', datos)

    return respuesta.data
}