import api from '../api/axios'

export type EstadoProveedor = 'ACTIVO' | 'INACTIVO'

export interface Proveedor {
    id: string
    empresaId: string
    rut: string | null
    razonSocial: string
    contacto: string | null
    email: string | null
    telefono: string | null
    direccion: string | null
    comuna: string | null
    region: string | null
    sitioWeb: string | null
    estado: EstadoProveedor
    fechaCreacion: string
    fechaActualizacion: string
}

export interface ProveedorRequest {
    rut: string
    razonSocial: string
    contacto: string
    email: string
    telefono: string
    direccion: string
    comuna: string
    region: string
    sitioWeb: string
}

export async function obtenerProveedores(): Promise<Proveedor[]> {
    const respuesta =
        await api.get<Proveedor[]>('/proveedores')

    return respuesta.data
}

export async function crearProveedor(
    datos: ProveedorRequest
): Promise<Proveedor> {
    const respuesta =
        await api.post<Proveedor>('/proveedores', datos)

    return respuesta.data
}

export async function actualizarProveedor(
    id: string,
    datos: ProveedorRequest
): Promise<Proveedor> {
    const respuesta =
        await api.put<Proveedor>(
            `/proveedores/${id}`,
            datos
        )

    return respuesta.data
}

export async function activarProveedor(
    id: string
): Promise<Proveedor> {
    const respuesta =
        await api.patch<Proveedor>(
            `/proveedores/${id}/activar`
        )

    return respuesta.data
}

export async function desactivarProveedor(
    id: string
): Promise<Proveedor> {
    const respuesta =
        await api.patch<Proveedor>(
            `/proveedores/${id}/desactivar`
        )

    return respuesta.data
}