import api from '../api/axios'

export type EstadoProducto =
    | 'ACTIVO'
    | 'INACTIVO'
    | 'DESCONTINUADO'

export interface Producto {
    id: string
    empresaId: string
    categoriaId: string
    categoriaNombre: string
    sku: string
    nombre: string
    descripcion: string | null
    precioCompra: number
    precioVenta: number
    unidadMedida: string
    estado: EstadoProducto
    fechaCreacion: string
    fechaActualizacion: string
}

export interface ProductoRequest {
    categoriaId: string
    sku: string
    nombre: string
    descripcion: string
    precioCompra: number
    precioVenta: number
    unidadMedida: string
}

export async function obtenerProductos(): Promise<Producto[]> {
    const respuesta = await api.get<Producto[]>('/productos')
    return respuesta.data
}

export async function crearProducto(
    datos: ProductoRequest
): Promise<Producto> {
    const respuesta = await api.post<Producto>('/productos', datos)
    return respuesta.data
}

export async function actualizarProducto(
    id: string,
    datos: ProductoRequest
): Promise<Producto> {
    const respuesta = await api.put<Producto>(`/productos/${id}`, datos)
    return respuesta.data
}

export async function activarProducto(id: string): Promise<Producto> {
    const respuesta = await api.patch<Producto>(`/productos/${id}/activar`)
    return respuesta.data
}

export async function desactivarProducto(id: string): Promise<Producto> {
    const respuesta = await api.patch<Producto>(`/productos/${id}/desactivar`)
    return respuesta.data
}

export async function descontinuarProducto(id: string): Promise<Producto> {
    const respuesta = await api.patch<Producto>(
        `/productos/${id}/descontinuar`
    )
    return respuesta.data
}