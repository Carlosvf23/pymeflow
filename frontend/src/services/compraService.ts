import api from '../api/axios'

export type EstadoCompra =
    | 'BORRADOR'
    | 'CONFIRMADA'
    | 'ANULADA'

export interface CrearDetalleCompraRequest {
    productoId: string
    cantidad: number
    precioUnitario: number
}

export interface CrearCompraRequest {
    proveedorId: string
    numeroDocumento: string
    fechaCompra: string
    observacion: string
    detalles: CrearDetalleCompraRequest[]
}

export interface DetalleCompra {
    id: string
    productoId: string
    productoNombre: string
    cantidad: number
    precioUnitario: number
    subtotal: number
}

export interface Compra {
    id: string
    proveedorId: string
    proveedorRazonSocial: string
    numeroDocumento: string | null
    fechaCompra: string
    estado: EstadoCompra
    total: number
    observacion: string | null
    detalles: DetalleCompra[]
    fechaCreacion: string
    fechaActualizacion: string
}

export async function obtenerCompras(): Promise<Compra[]> {
    const respuesta =
        await api.get<Compra[]>('/compras')

    return respuesta.data
}

export async function obtenerCompra(
    id: string
): Promise<Compra> {

    const respuesta =
        await api.get<Compra>(
            `/compras/${id}`
        )

    return respuesta.data
}

export async function crearCompra(
    datos: CrearCompraRequest
): Promise<Compra> {

    const respuesta =
        await api.post<Compra>(
            '/compras',
            datos
        )

    return respuesta.data
}

export async function actualizarCompra(
    id: string,
    datos: CrearCompraRequest
): Promise<Compra> {

    const respuesta =
        await api.put<Compra>(
            `/compras/${id}`,
            datos
        )

    return respuesta.data
}

export async function eliminarCompra(
    id: string
): Promise<void> {

    await api.delete(
        `/compras/${id}`
    )
}

export async function confirmarCompra(
    id: string
): Promise<Compra> {

    const respuesta =
        await api.patch<Compra>(
            `/compras/${id}/confirmar`
        )

    return respuesta.data
}