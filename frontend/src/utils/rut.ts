export function limpiarRut(rut: string): string {
    return rut
        .replace(/\./g, '')
        .replace(/-/g, '')
        .replace(/[^0-9kK]/g, '')
        .toUpperCase()
}

export function formatearRut(rut: string): string {
    const limpio = limpiarRut(rut)

    if (limpio.length <= 1) {
        return limpio
    }

    const cuerpo = limpio.slice(0, -1)
    const dv = limpio.slice(-1)

    const cuerpoFormateado =
        cuerpo.replace(
            /\B(?=(\d{3})+(?!\d))/g,
            '.'
        )

    return `${cuerpoFormateado}-${dv}`
}

export function validarRut(rut: string): boolean {
    const limpio = limpiarRut(rut)

    if (limpio.length < 2) {
        return false
    }

    const cuerpo = limpio.slice(0, -1)
    const dvIngresado = limpio.slice(-1)

    if (!/^\d+$/.test(cuerpo)) {
        return false
    }

    let suma = 0
    let multiplicador = 2

    for (let i = cuerpo.length - 1; i >= 0; i--) {
        suma += Number(cuerpo[i]) * multiplicador

        multiplicador =
            multiplicador === 7
                ? 2
                : multiplicador + 1
    }

    const resto = 11 - (suma % 11)

    let dvCalculado: string

    if (resto === 11) {
        dvCalculado = '0'
    } else if (resto === 10) {
        dvCalculado = 'K'
    } else {
        dvCalculado = String(resto)
    }

    return dvCalculado === dvIngresado
}