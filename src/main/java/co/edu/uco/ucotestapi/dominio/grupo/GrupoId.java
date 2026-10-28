package co.edu.uco.ucotestapi.dominio.grupo;

import java.util.Objects;
import java.util.UUID;

/** Identifica a un grupo. */
public record GrupoId(UUID valor) {

	/** Id de los Null Object: ningún registro real lo usa, así nadie compara contra {@code null} (A-11). */
	static final GrupoId NINGUNO = new GrupoId(new UUID(0L, 0L));

	public GrupoId {
		Objects.requireNonNull(valor, "valor");
	}
}
