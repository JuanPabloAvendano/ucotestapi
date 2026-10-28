<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { api, ErrorDeApi, type EstudianteDelGrupo, type Evaluacion, type Grupo } from './api'
import { cerrarSesion, nombreDelUsuario } from './autenticacion'
import { aCentesimas, aTexto } from './calificacion'

const grupos = ref<Grupo[]>([])
const evaluaciones = ref<Evaluacion[]>([])
const estudiantes = ref<EstudianteDelGrupo[]>([])

const grupoId = ref('')
const notaId = ref('')
const estudianteId = ref('')
const nota = ref('')

const enviando = ref(false)
const exito = ref('')
const error = ref('')
const traceId = ref('')

const centesimas = computed(() => aCentesimas(nota.value))
const estudianteElegido = computed(() => estudiantes.value.find((e) => e.id === estudianteId.value))
const grupoElegido = computed(() => grupos.value.find((g) => g.id === grupoId.value))
const puedeEnviar = computed(
  () =>
    !enviando.value &&
    grupoId.value !== '' &&
    notaId.value !== '' &&
    estudianteElegido.value !== undefined &&
    !estudianteElegido.value.tieneNota &&
    centesimas.value !== null,
)

function limpiarMensajes(): void {
  exito.value = ''
  error.value = ''
  traceId.value = ''
}

/** Q-20: se muestra el detail del problem+json y, si es 500, también el traceId para reportarlo. */
function mostrarError(fallo: unknown): void {
  if (fallo instanceof ErrorDeApi) {
    error.value = fallo.message
    traceId.value = fallo.problema.status >= 500 ? (fallo.problema.traceId ?? '') : ''
  } else {
    error.value = 'No pudimos comunicarnos con el servidor.'
  }
}

async function cargarEstudiantes(): Promise<void> {
  estudiantes.value = grupoId.value && notaId.value ? (await api.estudiantes(grupoId.value, notaId.value)).contenido : []
}

watch(grupoId, async (id) => {
  notaId.value = ''
  estudianteId.value = ''
  evaluaciones.value = []
  estudiantes.value = []
  limpiarMensajes()
  if (id) {
    try {
      evaluaciones.value = (await api.evaluaciones(id)).contenido
    } catch (fallo) {
      mostrarError(fallo)
    }
  }
})

watch(notaId, async () => {
  estudianteId.value = ''
  limpiarMensajes()
  try {
    await cargarEstudiantes()
  } catch (fallo) {
    mostrarError(fallo)
  }
})

async function registrar(): Promise<void> {
  if (!puedeEnviar.value || centesimas.value === null) {
    return
  }
  enviando.value = true
  limpiarMensajes()
  try {
    const registrada = await api.registrar(grupoId.value, notaId.value, estudianteId.value, centesimas.value)
    exito.value = `${registrada.mensaje} (${estudianteElegido.value?.nombre}: ${aTexto(registrada.centesimas)})`
    nota.value = ''
    await cargarEstudiantes()
  } catch (fallo) {
    mostrarError(fallo)
    await cargarEstudiantes().catch(() => undefined)
  } finally {
    enviando.value = false
  }
}

onMounted(async () => {
  try {
    grupos.value = (await api.grupos()).contenido
    if (grupos.value.length === 1 && grupos.value[0]) {
      grupoId.value = grupos.value[0].id
    }
  } catch (fallo) {
    mostrarError(fallo)
  }
})
</script>

<template>
  <main class="pagina">
    <header class="cabecera">
      <h1>Registrar calificación</h1>
      <div class="usuario">
        <span>{{ nombreDelUsuario() }}</span>
        <button type="button" class="enlace" @click="cerrarSesion">Cerrar sesión</button>
      </div>
    </header>

    <form class="formulario" @submit.prevent="registrar">
      <label>
        Grupo
        <select v-model="grupoId" required>
          <option value="" disabled>Elige un grupo</option>
          <option v-for="grupo in grupos" :key="grupo.id" :value="grupo.id">{{ grupo.nombre }}</option>
        </select>
      </label>
      <p v-if="grupoElegido" class="ayuda">
        Vigente del {{ grupoElegido.inicio }} al {{ grupoElegido.fin }} (fechas UTC)
      </p>

      <label>
        Evaluación
        <select v-model="notaId" :disabled="evaluaciones.length === 0" required>
          <option value="" disabled>Elige una evaluación</option>
          <option v-for="evaluacion in evaluaciones" :key="evaluacion.id" :value="evaluacion.id">
            {{ evaluacion.descripcion }}
          </option>
        </select>
      </label>

      <label>
        Estudiante
        <select v-model="estudianteId" :disabled="estudiantes.length === 0" required>
          <option value="" disabled>Elige un estudiante</option>
          <option v-for="estudiante in estudiantes" :key="estudiante.id" :value="estudiante.id" :disabled="estudiante.tieneNota">
            {{ estudiante.nombre }}{{ estudiante.tieneNota ? ' · ya tiene nota' : '' }}
          </option>
        </select>
      </label>

      <label>
        Calificación (0.00 a 5.00)
        <input v-model="nota" inputmode="decimal" placeholder="4.35" autocomplete="off" />
      </label>
      <p v-if="nota && centesimas === null" class="ayuda error">Escribe un número con máximo 2 decimales.</p>

      <button type="submit" :disabled="!puedeEnviar">{{ enviando ? 'Registrando…' : 'Registrar' }}</button>
    </form>

    <p v-if="exito" class="aviso exito" role="status">{{ exito }}</p>
    <p v-if="error" class="aviso error" role="alert">
      {{ error }}
      <span v-if="traceId" class="traza">Código de seguimiento: {{ traceId }}</span>
    </p>
  </main>
</template>
