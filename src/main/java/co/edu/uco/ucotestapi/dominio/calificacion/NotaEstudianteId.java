package co.edu.uco.ucotestapi.dominio.calificacion;

import java.util.Objects;
import java.util.UUID;

/** Identifica a una calificación registrada; siempre UUID v7 (A-4). */
public record NotaEstudianteId(UUID valor) {

	/** Id de los Null Object: ningún registro real lo usa, así nadie compara contra {@code null} (A-11). */
	static final NotaEstudianteId NINGUNO = new NotaEstudianteId(new UUID(0L, 0L));

	public NotaEstudianteId {
		Objects.requireNonNull(valor, "valor");
	}
}
