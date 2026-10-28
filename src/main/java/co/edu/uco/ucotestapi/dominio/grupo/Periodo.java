package co.edu.uco.ucotestapi.dominio.grupo;

import java.time.LocalDate;
import java.util.Objects;

/** Fechas en que se dicta un grupo, en días UTC con ambos extremos incluidos (A-42). */
public record Periodo(LocalDate inicio, LocalDate fin) {

	public Periodo {
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
