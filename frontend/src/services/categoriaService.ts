import api from '../api/axios'

export type EstadoCategoria = 'ACTIVA' | 'INACTIVA'

export interface Categoria {
    id: string
    empresaId: string
    nombre: string
    descripcion: string | null
    estado: EstadoCategoria
    fechaCreacion: string
    fechaActualizacion: string
}

export interface CategoriaRequest {
    nombre: string
    descripcion: string
}

export async function obtenerCategorias(): Promise<Categoria[]> {
    const respuesta = await api.get<Categoria[]>('/categorias')
    return respuesta.data
}

export async function crearCategoria(
    datos: CategoriaRequest
): Promise<Categoria> {
    const respuesta = await api.post<Categoria>('/categorias', datos)
    return respuesta.data
}

export async function actualizarCategoria(
    id: string,
    datos: CategoriaRequest
): Promise<Categoria> {
    const respuesta = await api.put<Categoria>(`/categorias/${id}`, datos)
    return respuesta.data
}

export async function activarCategoria(id: string): Promise<Categoria> {
    const respuesta = await api.patch<Categoria>(`/categorias/${id}/activar`)
    return respuesta.data
}

export async function desactivarCategoria(id: string): Promise<Categoria> {
    const respuesta = await api.patch<Categoria>(`/categorias/${id}/desactivar`)
    return respuesta.data
}