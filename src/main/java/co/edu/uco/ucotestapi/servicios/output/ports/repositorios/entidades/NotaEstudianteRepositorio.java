package co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;

/** Calificaciones registradas. */
public interface NotaEstudianteRepositorio {

	boolean existe(NotaId evaluacion, EstudianteId estudiante);

	/**
	 * Guarda dentro de la transacción en curso. Si el índice único de (evaluación, estudiante) rechaza la fila,
	 * lanza {@code ReglaIncumplida(M-NOTA-R06)} (A-32, A-62 (7)).
	 */
	NotaEstudiante guardar(NotaEstudiante calificacion);

	/** Si no existe, {@link NotaEstudiante#noExiste()}. */
	NotaEstudiante porId(NotaEstudianteId id);
}
