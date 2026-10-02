interface ModuloPageProps {
    titulo: string
    descripcion: string
}

function ModuloPage({
                        titulo,
                        descripcion
                    }: ModuloPageProps) {

    return (
        <main className="dashboard">
            <header className="dashboard-header">
                <h1>{titulo}</h1>
                <p>{descripcion}</p>
            </header>

            <section className="panel">
                <div className="panel-header">
                    <h2>Módulo {titulo}</h2>
                </div>

                <p className="sin-datos">
                    Próximamente implementaremos la gestión de {titulo.toLowerCase()}.
                </p>
            </section>
        </main>
    )
}

export default ModuloPage