package co.edu.uco.ucotestapi.dominio.grupo;

import java.util.Objects;
import java.util.UUID;

/** Identifica a una evaluación, el ítem calificable que el tablero llama «nota» (COH-14). */
public record NotaId(UUID valor) {

	/** Id de los Null Object: ningún registro real lo usa, así nadie compara contra {@code null} (A-11). */
	public static final NotaId NINGUNO = new NotaId(new UUID(0L, 0L));

	public NotaId {
		Objects.requireNonNull(valor, "valor");
	}
}
