import api from '../api/axios'

export interface LoginRequest {
    email: string
    password: string
}

export interface LoginResponse {
    token: string
    usuarioId: string
    empresaId: string
    nombre: string
    email: string
    rol: string
}

export async function login(
    datos: LoginRequest
): Promise<LoginResponse> {

    const respuesta =
        await api.post<LoginResponse>('/auth/login', datos)

    return respuesta.data
}