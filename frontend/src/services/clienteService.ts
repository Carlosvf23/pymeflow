import api from '../api/axios'

export type EstadoCliente =
    | 'ACTIVO'
    | 'INACTIVO'

export interface Cliente {
    id: string
    empresaId: string
    rut: string | null
    razonSocial: string
    email: string | null
    telefono: string | null
    direccion: string | null
    comuna: string | null
    region: string | null
    estado: EstadoCliente
    fechaCreacion: string
    fechaActualizacion: string
}

export interface ClienteRequest {
    rut: string
    razonSocial: string
    email: string
    telefono: string
    direccion: string
    comuna: string
    region: string
}

export async function obtenerClientes(): Promise<Cliente[]> {
    const respuesta =
        await api.get<Cliente[]>('/clientes')

    return respuesta.data
}

export async function crearCliente(
    datos: ClienteRequest
): Promise<Cliente> {

    const respuesta =
        await api.post<Cliente>('/clientes', datos)

    return respuesta.data
}

export async function actualizarCliente(
    id: string,
    datos: ClienteRequest
): Promise<Cliente> {

    const respuesta =
        await api.put<Cliente>(
            `/clientes/${id}`,
            datos
        )

    return respuesta.data
}
export async function activarCliente(id: string): Promise<Cliente> {
    const respuesta = await api.patch<Cliente>(
        `/clientes/${id}/activar`
    )

    return respuesta.data
}

export async function desactivarCliente(id: string): Promise<Cliente> {
    const respuesta = await api.patch<Cliente>(
        `/clientes/${id}/desactivar`
    )

    return respuesta.data
}