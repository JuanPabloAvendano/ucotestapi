package co.edu.uco.ucotestapi.dominio.grupo;

import java.util.Objects;

/** Evaluación: el ítem calificable de un grupo, como «Parcial 1» (R-4; COH-14: no es la calificación). */
public final class Nota {

	private static final Nota NO_EXISTE = new Nota(NotaId.NINGUNO, GrupoId.NINGUNO, "");

	private final NotaId id;
	private final GrupoId grupo;
	private final String descripcion;

	private Nota(NotaId id, GrupoId grupo, String descripcion) {
		this.id = Objects.requireNonNull(id, "id");
		this.grupo = Objects.requireNonNull(grupo, "grupo");
		this.descripcion = Objects.requireNonNull(descripcion, "descripcion");
	}

	public static Nota existente(NotaId id, GrupoId grupo, String descripcion) {
		return new Nota(id, grupo, descripcion);
	}

	/** Null Object (A-11): no pertenece a ningún grupo. */
	public static Nota noExiste() {
		return NO_EXISTE;
	}

	public boolean existe() {
		return this != NO_EXISTE;
	}

	public boolean perteneceA(GrupoId otroGrupo) {
		return existe() && grupo.equals(otroGrupo);
	}

	public NotaId id() {
		return id;
	}

	public GrupoId grupo() {
		return grupo;
	}

	public String descripcion() {
		return descripcion;
	}
}
