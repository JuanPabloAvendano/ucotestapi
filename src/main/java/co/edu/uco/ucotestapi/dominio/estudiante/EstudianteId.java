package co.edu.uco.ucotestapi.dominio.estudiante;

import java.util.Objects;
import java.util.UUID;

/** Identifica a un estudiante (A-21). */
public record EstudianteId(UUID valor) {

	/** Id de los Null Object: ningún registro real lo usa, así nadie compara contra {@code null} (A-11). */
	public static final EstudianteId NINGUNO = new EstudianteId(new UUID(0L, 0L));

	public EstudianteId {
		Objects.requireNonNull(valor, "valor");
	}
}
