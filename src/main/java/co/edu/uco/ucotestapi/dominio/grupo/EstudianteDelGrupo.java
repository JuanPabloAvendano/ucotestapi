package co.edu.uco.ucotestapi.dominio.grupo;

import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;

/** Fila de la consulta de estudiantes de un grupo (A-24, Q-13); {@code tieneNota} es para la evaluación elegida. */
public record EstudianteDelGrupo(EstudianteId id, String nombre, boolean tieneNota) {
}
