package co.edu.uco.ucotestapi.dominio.grupo;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;

/** Matrícula: un estudiante inscrito en un grupo (R-1); es la fila que se bloquea contra el registro doble (A-22). */
public final class EstudianteGrupo {

	private static final EstudianteGrupo NO_EXISTE = new EstudianteGrupo(EstudianteGrupoId.NINGUNO,
			EstudianteId.NINGUNO, GrupoId.NINGUNO);

	private final EstudianteGrupoId id;
	private final EstudianteId estudiante;
	private final GrupoId grupo;

	private EstudianteGrupo(EstudianteGrupoId id, EstudianteId estudiante, GrupoId grupo) {
		this.id = Objects.requireNonNull(id, "id");
		this.estudiante = Objects.requireNonNull(estudiante, "estudiante");
		this.grupo = Objects.requireNonNull(grupo, "grupo");
	}

	public static EstudianteGrupo existente(EstudianteGrupoId id, EstudianteId estudiante, GrupoId grupo) {
		return new EstudianteGrupo(id, estudiante, grupo);
	}

	/** Null Object (A-11): el estudiante no está matriculado en el grupo. */
	public static EstudianteGrupo noExiste() {
		return NO_EXISTE;
	}

	public boolean existe() {
		return this != NO_EXISTE;
	}

	public boolean matriculaA(EstudianteId otroEstudiante, GrupoId otroGrupo) {
		return existe() && estudiante.equals(otroEstudiante) && grupo.equals(otroGrupo);
	}

	public EstudianteGrupoId id() {
		return id;
	}

	public EstudianteId estudiante() {
		return estudiante;
	}

	public GrupoId grupo() {
		return grupo;
	}
}
