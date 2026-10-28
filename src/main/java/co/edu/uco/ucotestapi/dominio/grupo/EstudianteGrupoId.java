package co.edu.uco.ucotestapi.dominio.grupo;

import java.util.Objects;
import java.util.UUID;

/** Identifica a una matrícula: un estudiante en un grupo. */
public record EstudianteGrupoId(UUID valor) {

	/** Id de los Null Object: ningún registro real lo usa, así nadie compara contra {@code null} (A-11). */
	static final EstudianteGrupoId NINGUNO = new EstudianteGrupoId(new UUID(0L, 0L));

	public EstudianteGrupoId {
		Objects.requireNonNull(valor, "valor");
	}
}
