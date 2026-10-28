package co.edu.uco.ucotestapi.dominio.calificacion;

import java.time.LocalDate;
import java.util.Objects;

/** Intervalo en que se permite registrar calificaciones, en días UTC con ambos extremos incluidos (A-40). */
public record FechasDeRegistro(LocalDate inicio, LocalDate fin) {

	public FechasDeRegistro {
		Objects.requireNonNull(inicio, "inicio");
		Objects.requireNonNull(fin, "fin");
		if (inicio.isAfter(fin)) {
			throw new IllegalArgumentException("el inicio no puede ser posterior al fin");
		}
	}

	public boolean contiene(LocalDate dia) {
		return !dia.isBefore(inicio) && !dia.isAfter(fin);
	}
}
