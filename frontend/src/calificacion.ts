/**
 * Convierte lo que escribe el docente (4.35) a centésimas (435) sin pasar por punto flotante, que podría dejar
 * decimales ocultos (A-19). Acepta coma o punto y hasta 2 decimales; devuelve null si no es una nota legible.
 * El rango 0..5 lo decide el backend (R-9); aquí solo se revisa la forma.
 */
export function aCentesimas(texto: string): number | null {
  const limpio = texto.trim().replace(',', '.')
  const partes = /^(\d{1,3})(?:\.(\d{1,2}))?$/.exec(limpio)
  if (!partes) {
    return null
  }
  const enteros = Number(partes[1])
  const decimales = Number((partes[2] ?? '').padEnd(2, '0'))
  return enteros * 100 + decimales
}

/** 435 → «4.35» (el escalado hacia abajo solo ocurre al mostrar, A-36). */
export function aTexto(centesimas: number): string {
  return (centesimas / 100).toFixed(2)
}
