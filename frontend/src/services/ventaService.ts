import api from '../api/axios'

export type EstadoVenta =
    | 'BORRADOR'
    | 'CONFIRMADA'
    | 'ANULADA'

export interface CrearDetalleVentaRequest {
    productoId: string
    cantidad: number
    precioUnitario: number
}

export interface CrearVentaRequest {
    clienteId: string
    numeroDocumento: string
    fechaVenta: string
    observacion: string
    detalles: CrearDetalleVentaRequest[]
}

export interface DetalleVenta {
    id: string
    productoId: string
    productoNombre: string
    cantidad: number
    precioUnitario: number
    subtotal: number
}

export interface Venta {
    id: string
    clienteId: string
    clienteRazonSocial: string
    numeroDocumento: string | null
    fechaVenta: string
    estado: EstadoVenta
    total: number
    observacion: string | null
    detalles: DetalleVenta[]
    fechaCreacion: string
    fechaActualizacion: string
}

export async function obtenerVentas(): Promise<Venta[]> {
    const respuesta =
        await api.get<Venta[]>('/ventas')

    return respuesta.data
}

export async function obtenerVenta(
    id: string
): Promise<Venta> {
    const respuesta =
        await api.get<Venta>(`/ventas/${id}`)

    return respuesta.data
}

export async function crearVenta(
    datos: CrearVentaRequest
): Promise<Venta> {
    const respuesta =
        await api.post<Venta>('/ventas', datos)

    return respuesta.data
}

export async function confirmarVenta(
    id: string
): Promise<Venta> {
    const respuesta =
        await api.patch<Venta>(
            `/ventas/${id}/confirmar`
        )

    return respuesta.data
}