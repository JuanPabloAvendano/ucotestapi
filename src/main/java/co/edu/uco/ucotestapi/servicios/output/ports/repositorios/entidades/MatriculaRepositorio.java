package co.edu.uco.ucotestapi.servicios.output.ports.repositorios.entidades;

import co.edu.uco.ucotestapi.dominio.estudiante.EstudianteId;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteDelGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.transversal.Pagina;

/** Matrículas ({@link EstudianteGrupo}). */
public interface MatriculaRepositorio {

	/** Si no está matriculado, {@link EstudianteGrupo#noExiste()}. */
	EstudianteGrupo de(EstudianteId estudiante, GrupoId grupo);

	/**
	 * Bloquea la fila de la matrícula hasta el fin de la transacción en curso (A-22); solo tiene sentido dentro de
	 * {@code UnidadDeTrabajo.ejecutar}. Si no hay fila, {@link EstudianteGrupo#noExiste()}.
	 */
	EstudianteGrupo bloquear(EstudianteId estudiante, GrupoId grupo);

	Pagina<EstudianteDelGrupo> estudiantesDe(GrupoId grupo, NotaId evaluacion, int pagina, int tamano);
}
