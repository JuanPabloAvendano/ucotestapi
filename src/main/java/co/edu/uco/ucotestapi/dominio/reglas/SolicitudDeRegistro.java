package co.edu.uco.ucotestapi.dominio.reglas;

import java.time.LocalDate;
import java.util.Objects;

import co.edu.uco.ucotestapi.dominio.calificacion.Calificacion;
import co.edu.uco.ucotestapi.dominio.calificacion.FechasDeRegistro;
import co.edu.uco.ucotestapi.dominio.calificacion.RangoCalificacion;
import co.edu.uco.ucotestapi.dominio.docente.Docente;
import co.edu.uco.ucotestapi.dominio.estudiante.Estudiante;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;

/**
 * Todo lo que las reglas R-0..R-9 necesitan mirar, ya cargado por el caso de uso. Lo que no existe llega como
 * Null Object (A-11), así cada especificación responde sin preguntar por {@code null}.
 */
public record SolicitudDeRegistro(Docente docente, Grupo grupo, Nota evaluacion, Estudiante estudiante,
		EstudianteGrupo matricula, Calificacion calificacion, RangoCalificacion rango, FechasDeRegistro fechas,
		LocalDate hoy) {

	public SolicitudDeRegistro {
		Objects.requireNonNull(docente, "docente");
		Objects.requireNonNull(grupo, "grupo");
		Objects.requireNonNull(evaluacion, "evaluacion");
		Objects.requireNonNull(estudiante, "estudiante");
		Objects.requireNonNull(matricula, "matricula");
		Objects.requireNonNull(calificacion, "calificacion");
		Objects.requireNonNull(rango, "rango");
		Objects.requireNonNull(fechas, "fechas");
		Objects.requireNonNull(hoy, "hoy");
	}
}
