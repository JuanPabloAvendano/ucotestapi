package co.edu.uco.ucotestapi.servicios.casosuso;

import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudiante;
import co.edu.uco.ucotestapi.dominio.calificacion.NotaEstudianteId;
import co.edu.uco.ucotestapi.dominio.cuenta.CuentaId;
import co.edu.uco.ucotestapi.dominio.grupo.EstudianteDelGrupo;
import co.edu.uco.ucotestapi.dominio.grupo.Grupo;
import co.edu.uco.ucotestapi.dominio.grupo.GrupoId;
import co.edu.uco.ucotestapi.dominio.grupo.Nota;
import co.edu.uco.ucotestapi.dominio.grupo.NotaId;
import co.edu.uco.ucotestapi.transversal.Pagina;

/**
 * Consultas de solo lectura que el formulario necesita (A-24, Q-13, Q-15). Son otro elemento (§7): sin reglas de
 * negocio, salvo que el grupo sea del docente del token.
 *
 * @throws co.edu.uco.ucotestapi.servicios.casosuso.ConsultaNoPermitida si el grupo no es del docente
 */
public interface ConsultarClaseDelDocenteCasoUso {

	Pagina<Grupo> gruposDelDocente(CuentaId cuentaDocente, int pagina, int tamano);

	Pagina<Nota> evaluacionesDelGrupo(CuentaId cuentaDocente, GrupoId grupo, int pagina, int tamano);

	Pagina<EstudianteDelGrupo> estudiantesDelGrupo(CuentaId cuentaDocente, GrupoId grupo, NotaId evaluacion,
			int pagina, int tamano);

	/** @throws co.edu.uco.ucotestapi.servicios.casosuso.CalificacionNoEncontrada si no existe en esa evaluación del grupo */
	NotaEstudiante calificacion(CuentaId cuentaDocente, GrupoId grupo, NotaId evaluacion, NotaEstudianteId id);
}
