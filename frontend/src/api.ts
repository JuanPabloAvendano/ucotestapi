import { tokenVigente } from './autenticacion'

export interface Pagina<T> {
  contenido: T[]
  pagina: number
  tamano: number
  total: number
}

export interface Grupo {
  id: string
  nombre: string
  inicio: string
  fin: string
}

export interface Evaluacion {
  id: string
  descripcion: string
}

export interface EstudianteDelGrupo {
  id: string
  nombre: string
  tieneNota: boolean
}

export interface CalificacionRegistrada {
  id: string
  centesimas: number
  mensaje: string
}

/** problem+json del backend (A-20). */
export interface Problema {
  status: number
  detail?: string
  codigo?: string
  traceId?: string
}

export class ErrorDeApi extends Error {
  constructor(readonly problema: Problema) {
    super(problema.detail ?? `Error ${problema.status}`)
  }
}

async function pedir<T>(ruta: string, opciones: RequestInit = {}): Promise<T> {
  const respuesta = await fetch(ruta, {
    ...opciones,
    headers: {
      ...opciones.headers,
      Authorization: `Bearer ${await tokenVigente()}`,
      Accept: 'application/json, application/problem+json',
    },
  })
  if (!respuesta.ok) {
    const problema: Problema = respuesta.headers.get('Content-Type')?.includes('json')
      ? await respuesta.json()
      : { status: respuesta.status }
    throw new ErrorDeApi({ ...problema, status: respuesta.status })
  }
  return (await respuesta.json()) as T
}

export const api = {
  grupos: () => pedir<Pagina<Grupo>>('/api/v1/docentes/yo/grupos'),
  evaluaciones: (grupoId: string) => pedir<Pagina<Evaluacion>>(`/api/v1/grupos/${grupoId}/notas`),
  estudiantes: (grupoId: string, notaId: string) =>
    pedir<Pagina<EstudianteDelGrupo>>(`/api/v1/grupos/${grupoId}/estudiantes?notaId=${encodeURIComponent(notaId)}`),
  registrar: (grupoId: string, notaId: string, estudianteId: string, centesimas: number) =>
    pedir<CalificacionRegistrada>(`/api/v1/grupos/${grupoId}/notas/${notaId}/calificaciones`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estudianteId, centesimas }),
    }),
}
