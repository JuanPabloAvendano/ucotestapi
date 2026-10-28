package co.edu.uco.ucotestapi.servicios.casosuso;

/** La calificación pedida por el {@code Location} no existe en esa evaluación (Q-15): sale como 404. */
public class CalificacionNoEncontrada extends RuntimeException {

	public CalificacionNoEncontrada() {
		super("calificación no encontrada");
	}
}
