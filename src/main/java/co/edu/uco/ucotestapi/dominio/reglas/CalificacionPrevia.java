package co.edu.uco.ucotestapi.dominio.reglas;

/** Lo que R-6 mira: si el estudiante ya tiene calificación en la evaluación, leído dentro del bloqueo (A-22). */
public record CalificacionPrevia(boolean existe) {
}
