package co.edu.uco.ucotestapi.dominio.calificacion;

import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;

/** Calificación registrada: el valor que saca un estudiante en una evaluación (R-11). */
public final class NotaEstudiante {

	private static final NotaEstudiante NO_EXISTE = new NotaEstudiante(NotaEstudianteId.NINGUNO, NotaId.NINGUNO,
			EstudianteId.NINGUNO, new Calificacion(0));

	private final NotaEstudianteId id;
	private final NotaId nota;
	private final EstudianteId estudiante;
	private final Calificacion calificacion;

	private NotaEstudiante(NotaEstudianteId id, NotaId nota, EstudianteId estudiante, Calificacion calificacion) {
		this.id = Objects.requireNonNull(id, "id");
		this.nota = Objects.requireNonNull(nota, "nota");
		this.estudiante = Objects.requireNonNull(estudiante, "estudiante");
		this.calificacion = Objects.requireNonNull(calificacion, "calificacion");
	}

	public static NotaEstudiante registrar(NotaEstudianteId id, NotaId nota, EstudianteId estudiante,
			Calificacion calificacion) {
		return new NotaEstudiante(id, nota, estudiante, calificacion);
	}

	/** Null Object (A-11) para la consulta por id (Q-15). */
	public static NotaEstudiante noExiste() {
		return NO_EXISTE;
	}

	public boolean existe() {
		return this != NO_EXISTE;
	}

	public boolean esDeLaEvaluacion(NotaId otraNota) {
		return existe() && nota.equals(otraNota);
	}

	public NotaEstudianteId id() {
		return id;
	}

	public NotaId nota() {
		return nota;
	}

	public EstudianteId estudiante() {
		return estudiante;
	}

	public Calificacion calificacion() {
		return calificacion;
	}
}
