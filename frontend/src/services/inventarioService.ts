import api from '../api/axios'

export type TipoMovimientoInventario =
    | 'ENTRADA_INICIAL'
    | 'ENTRADA_COMPRA'
    | 'SALIDA_VENTA'
    | 'AJUSTE_POSITIVO'
    | 'AJUSTE_NEGATIVO'

export interface StockProducto {
    productoId: string
    stock: number
}

export interface MovimientoInventario {
    id: string
    productoId: string
    productoNombre: string
    tipo: TipoMovimientoInventario
    cantidad: number
    observacion: string | null
    fechaMovimiento: string
}

export interface RegistrarMovimientoRequest {
    tipo:
        | 'ENTRADA_INICIAL'
        | 'AJUSTE_POSITIVO'
        | 'AJUSTE_NEGATIVO'
    cantidad: number
    observacion: string
}

export async function obtenerStock(
    productoId: string
): Promise<StockProducto> {
    const respuesta = await api.get<StockProducto>(
        `/inventario/productos/${productoId}/stock`
    )

    return respuesta.data
}

export async function obtenerMovimientos(
    productoId: string
): Promise<MovimientoInventario[]> {
    const respuesta = await api.get<MovimientoInventario[]>(
        `/inventario/productos/${productoId}/movimientos`
    )

    return respuesta.data
}

export async function registrarMovimiento(
    productoId: string,
    datos: RegistrarMovimientoRequest
): Promise<MovimientoInventario> {
    const respuesta = await api.post<MovimientoInventario>(
        `/inventario/productos/${productoId}/movimientos`,
        datos
    )

    return respuesta.data
}