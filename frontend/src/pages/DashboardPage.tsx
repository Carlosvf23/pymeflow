import { useEffect, useState } from 'react'
import {
    obtenerDashboard,
    type DashboardResponse
} from '../services/dashboardService'
import './DashboardPage.css'

function formatearCLP(valor: number) {
    return new Intl.NumberFormat('es-CL', {
        style: 'currency',
        currency: 'CLP',
        maximumFractionDigits: 0
    }).format(valor)
}

function formatearFecha(fecha: string) {
    return new Intl.DateTimeFormat('es-CL', {
        dateStyle: 'short',
        timeStyle: 'short'
    }).format(new Date(fecha))
}

function DashboardPage() {

    const [dashboard, setDashboard] =
        useState<DashboardResponse | null>(null)

    const [error, setError] = useState('')

    useEffect(() => {

        async function cargarDashboard() {
            try {
                const datos = await obtenerDashboard()
                setDashboard(datos)
            } catch {
                setError('No fue posible cargar el dashboard.')
            }
        }

        cargarDashboard()

    }, [])

    if (error) {
        return (
            <div className="dashboard-mensaje">
                <h2>Error</h2>
                <p>{error}</p>
            </div>
        )
    }

    if (!dashboard) {
        return (
            <div className="dashboard-mensaje">
                Cargando dashboard...
            </div>
        )
    }

    return (
        <main className="dashboard">

            <header className="dashboard-header">
                <div>
                    <h1>Dashboard</h1>
                    <p>Resumen general de tu empresa</p>
                </div>
            </header>

            <section className="metricas">

                <article className="tarjeta">
                    <span>Ventas confirmadas</span>
                    <strong>
                        {formatearCLP(dashboard.totalVentas)}
                    </strong>
                </article>

                <article className="tarjeta">
                    <span>Compras confirmadas</span>
                    <strong>
                        {formatearCLP(dashboard.totalCompras)}
                    </strong>
                </article>

                <article className="tarjeta">
                    <span>Clientes</span>
                    <strong>{dashboard.cantidadClientes}</strong>
                </article>

                <article className="tarjeta">
                    <span>Proveedores</span>
                    <strong>{dashboard.cantidadProveedores}</strong>
                </article>

                <article className="tarjeta">
                    <span>Productos</span>
                    <strong>{dashboard.cantidadProductos}</strong>
                </article>

            </section>

            <section className="dashboard-tablas">

                <article className="panel">

                    <div className="panel-header">
                        <h2>Últimas ventas</h2>
                    </div>

                    {dashboard.ultimasVentas.length === 0 ? (
                        <p className="sin-datos">
                            No hay ventas registradas.
                        </p>
                    ) : (
                        <div className="tabla-contenedor">
                            <table>
                                <thead>
                                <tr>
                                    <th>Documento</th>
                                    <th>Cliente</th>
                                    <th>Fecha</th>
                                    <th>Total</th>
                                </tr>
                                </thead>

                                <tbody>
                                {dashboard.ultimasVentas.map((venta) => (
                                    <tr key={venta.id}>
                                        <td>
                                            {venta.numeroDocumento || '-'}
                                        </td>
                                        <td>{venta.cliente}</td>
                                        <td>
                                            {formatearFecha(venta.fecha)}
                                        </td>
                                        <td>
                                            {formatearCLP(venta.total)}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                </article>

                <article className="panel">

                    <div className="panel-header">
                        <h2>Últimas compras</h2>
                    </div>

                    {dashboard.ultimasCompras.length === 0 ? (
                        <p className="sin-datos">
                            No hay compras registradas.
                        </p>
                    ) : (
                        <div className="tabla-contenedor">
                            <table>
                                <thead>
                                <tr>
                                    <th>Documento</th>
                                    <th>Proveedor</th>
                                    <th>Fecha</th>
                                    <th>Total</th>
                                </tr>
                                </thead>

                                <tbody>
                                {dashboard.ultimasCompras.map((compra) => (
                                    <tr key={compra.id}>
                                        <td>
                                            {compra.numeroDocumento || '-'}
                                        </td>
                                        <td>{compra.proveedor}</td>
                                        <td>
                                            {formatearFecha(compra.fecha)}
                                        </td>
                                        <td>
                                            {formatearCLP(compra.total)}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                </article>

            </section>

        </main>
    )
}

export default DashboardPage