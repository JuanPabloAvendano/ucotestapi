package co.edu.uco.ucotestapi.dominio.calificacion;

/** Mínimo y máximo de una calificación en centésimas, leídos del parameter catalog (A-36). */
public record RangoCalificacion(int minimo, int maximo) {

	public RangoCalificacion {
		if (minimo > maximo) {
			throw new IllegalArgumentException("el mínimo no puede ser mayor que el máximo");
		}
	}

	public boolean contiene(Calificacion calificacion) {
		return calificacion.centesimas() >= minimo && calificacion.centesimas() <= maximo;
	}
}
