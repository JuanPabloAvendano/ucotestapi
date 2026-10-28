package co.edu.uco.ucotestapi.dominio.calificacion;

/**
 * Calificación en centésimas: 435 es 4.35 (A-5, A-19). No valida el rango: eso lo decide R-9 con el rango
 * del parameter catalog (A-36), para que un valor fuera de rango sea un 422 con su código y no un error técnico.
 */
public record Calificacion(int centesimas) {
}
