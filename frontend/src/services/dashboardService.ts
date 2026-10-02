import api from '../api/axios'

export interface VentaResumen {
    id: string
    numeroDocumento: string
    cliente: string
    total: number
    fecha: string
}

export interface CompraResumen {
    id: string
    numeroDocumento: string
    proveedor: string
    total: number
    fecha: string
}

export interface DashboardResponse {
    totalVentas: number
    totalCompras: number
    cantidadClientes: number
    cantidadProveedores: number
    cantidadProductos: number
    ultimasVentas: VentaResumen[]
    ultimasCompras: CompraResumen[]
}

export async function obtenerDashboard(): Promise<DashboardResponse> {
    const respuesta = await api.get<DashboardResponse>('/dashboard')

    return respuesta.data
}