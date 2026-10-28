package co.edu.uco.ucotestapi.dominio.docente;

import java.util.Objects;
import java.util.UUID;

/** Identifica a un docente en las transacciones; no es el {@code sub} del token (A-21). */
public record DocenteId(UUID valor) {

	/** Id de los Null Object: ningún registro real lo usa, así nadie compara contra {@code null} (A-11). */
	public static final DocenteId NINGUNO = new DocenteId(new UUID(0L, 0L));

	public DocenteId {
		Objects.requireNonNull(valor, "valor");
	}
}
